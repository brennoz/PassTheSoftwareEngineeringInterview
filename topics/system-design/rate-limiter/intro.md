# Rate Limiter

## What it is
Rate limiting controls how often a client can call an API in a time window. Without it, one bad actor (or bug) saturates your system and degrades service for everyone. Used everywhere: API gateways, login endpoints, payment flows, SMS senders. The challenge is doing it correctly at distributed scale — a single in-memory counter only works for one server.

---

## Core Concepts

- **Token Bucket** — bucket holds N tokens, refills at fixed rate. Each request consumes 1 token. Allows short bursts up to bucket capacity. Most common in practice.
- **Leaky Bucket** — requests enter a queue (the bucket), drain at fixed rate. Smooths traffic, no bursts. Good for output rate control (e.g. email sender).
- **Fixed Window Counter** — count requests per fixed 1-min slot. Simple but has boundary spike: double traffic possible at window edge (59s + 1s straddle).
- **Sliding Window Log** — store timestamp of each request in a sorted set. Count entries in last 60s. Accurate, but O(N) memory per user.
- **Sliding Window Counter** — hybrid: current window count + (previous window count × overlap ratio). O(1) memory, ~1% error. Best balance.
- **Distributed rate limiting** — Redis is the standard. Atomic Lua script or `INCR + EXPIRE` avoids race conditions across multiple app servers.

---

## Code Samples

### Token Bucket (pure Java)
```java
// Algorithm: Token Bucket — O(1) per request, refill on access (lazy refill)
public class TokenBucket {
    private final long capacity;
    private final double refillRatePerMs;
    private double tokens;
    private long lastRefillTimestamp;

    public TokenBucket(long capacity, long refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerMs = refillRatePerSecond / 1000.0;
        this.tokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = (now - lastRefillTimestamp) * refillRatePerMs;
        tokens = Math.min(capacity, tokens + tokensToAdd);
        lastRefillTimestamp = now;
    }
}
```

### Redis Sliding Window Counter (Lua — atomic)
```lua
-- Key: "rate:{userId}"
-- Args: KEYS[1]=key, ARGV[1]=windowSizeSeconds, ARGV[2]=maxRequests, ARGV[3]=now
local key = KEYS[1]
local window = tonumber(ARGV[1])
local limit = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

-- Remove entries older than window
redis.call('ZREMRANGEBYSCORE', key, 0, now - window * 1000)
local count = redis.call('ZCARD', key)

if count < limit then
    redis.call('ZADD', key, now, now)           -- score=timestamp, member=timestamp
    redis.call('PEXPIRE', key, window * 1000)
    return 1  -- allowed
end
return 0  -- rejected
```
> Why Lua? Redis executes it atomically — no race between ZCARD and ZADD.

### Spring Boot Rate Limit Filter
```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redis;
    private final RateLimiterScript script;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String clientId = resolveClientId(req);  // IP or JWT subject
        boolean allowed = script.isAllowed(clientId);

        if (!allowed) {
            res.setStatus(429);
            res.getWriter().write("{\"error\":\"Too Many Requests\"}");
            return;
        }
        chain.doFilter(req, res);
    }
}
```

---

## Common Patterns

### 1. Per-user + per-endpoint limits
Different resources have different budgets. POST /transfer: 10/min. GET /balance: 200/min. Store separate keys: `rate:{userId}:{endpoint}`.

### 2. Tiered limits (free vs paid)
```
rate:{tier}:{userId} → different limit constants per tier
```
Resolve tier from JWT claim or DB lookup cached in Redis.

### 3. Distributed token bucket with Redis
```
EVAL luaScript 1 rate:{userId} {capacity} {refillRate} {now}
```
Stores `{tokens, lastRefill}` as Redis hash. Lazy refill on each request. Atomic via Lua.

---

## Interview Angles

| Question | Key Point |
|---|---|
| "Which algorithm would you pick?" | Token bucket for APIs (burst-friendly), leaky bucket for outbound rate (SMS, email) |
| "Why not fixed window?" | Boundary spike — attacker can double-tap at window edge |
| "How do you handle 100 app servers?" | Redis as shared state. In-memory only works single-node. |
| "Race condition in Redis?" | Atomic Lua script or Redis INCR+EXPIRE in one pipeline. Never read-check-write without atomicity. |
| "How do you return useful headers?" | `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`, `Retry-After` |
| "What HTTP status?" | 429 Too Many Requests |
| "Token bucket vs sliding window log?" | Token bucket O(1) memory, sliding log is accurate but O(requests) memory — bad at scale |
| "Sliding window counter tradeoff?" | ~1% error at window boundary in exchange for O(1) memory per user — usually acceptable |
| "What if Redis is down?" | Fail open (allow requests, log warning) or fail closed (block). Business decision. Circuit breaker pattern applies. |
| "Where does the limiter live?" | API Gateway level (centralized), not app-level (duplicated, inconsistent). |
