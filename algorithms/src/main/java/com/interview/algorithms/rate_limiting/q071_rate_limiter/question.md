# q071 — Rate Limiter: three implementations behind one interface

**Category:** algo · **Difficulty:** medium · **Module:** `algorithms`

**Lessons:** [Rate Limiter — algorithms & tradeoffs](../../../../../../topics/system-design/rate-limiter/intro.md)

---

## Problem

Given this interface:

```java
public interface RateLimiter {
    boolean limit();
}
```

Write **three** implementations, each a different algorithm:

1. `TokenBucketRateLimiter` — burst-friendly, O(1) state, lazy refill.
2. `FixedWindowRateLimiter` — simplest possible; keep (and be able to explain) the boundary-spike flaw.
3. `SlidingWindowLogRateLimiter` — exact, no boundary spike, O(maxRequests) state.

## Contract (pinned — the interface name is ambiguous)

- `limit()` returns `true` = **allowed** (one unit of budget consumed); `false` = **throttled**.
  Callers write `if (rl.limit()) { proceed(); } else { return 429; }`.
- One instance guards **one logical key**. No key argument. Limit config (capacity / window /
  refill rate) is passed to the **constructor**.
- **Thread-safe**: `limit()` is called from multiple threads. The check-then-act on the
  shared counter is a race — guard it.
- **Injected time**: constructor takes a `java.util.function.LongSupplier` for "now in
  millis" (plus a convenience constructor wiring `System::currentTimeMillis`). Tests drive a
  mutable fake clock — **no `Thread.sleep`**.
- **Tests**: JUnit 5 `Assertions` only. No AssertJ, no Mockito. 3–4 tests per implementation
  — allow-up-to-limit, reject-over-limit, recover-after-time; Fixed Window also gets a test
  that demonstrates the boundary spike.

## In AWS / distributed environment

- All three hold state in one JVM heap. On N pods the effective global limit is `N ×` the
  configured limit and drifts as pods autoscale.
- **Fixed Window** → Redis `INCR rate:{key}:{windowId}` + `EXPIRE` (ElastiCache). `INCR` is
  atomic; the boundary spike survives the port unchanged (it is an algorithm property).
- **Sliding Window Log** → Redis sorted set: `ZREMRANGEBYSCORE` + `ZCARD` + `ZADD` in one
  Lua script. Same O(maxRequests) memory, now in Redis RAM × active keys.
- **Token Bucket** → `{tokens, lastRefillMillis}` in a Redis hash, read-refill-consume in one
  Lua script so it is atomic without an app-side lock.
- **Redis down?** Business decision: fail-open (allow, log) or fail-closed (block). Wrap the
  Redis call in a circuit breaker either way.

---

## Deliverables

| Path | What |
|---|---|
| `answer/` | your implementations + tests (start from `.gitkeep`, write from scratch) |
| `solution/` | complete, heavily-commented reference — study it if you get stuck |
| `solution/RateLimiterMain.java` | runnable demo: all three on identical input, side by side |

`solution/` is **reference-only** — this question is not graded via the `next` flow.
