package com.interview.algorithms.rate_limiting.q071_rate_limiter.solution;

import java.util.function.LongSupplier;

import com.interview.algorithms.rate_limiting.q071_rate_limiter.RateLimiter;

/**
 * Token Bucket.
 *
 * <p>Mental model: a bucket holds up to {@code capacity} tokens and is topped up at a
 * steady {@code refillTokensPerSecond}. Every allowed call removes one token. An empty
 * bucket means "throttled". A full bucket that has been idle lets a client fire a burst of
 * {@code capacity} calls back-to-back, then settle to the refill rate — which is exactly
 * what you want for a public API: bursty clients are normal, sustained floods are not.
 *
 * <h2>Design decision — lazy ("on read") refill</h2>
 * There is no background thread adding tokens on a timer. Instead {@link #refill()} runs at
 * the top of every {@link #limit()} call and credits {@code elapsedMillis * ratePerMs}
 * tokens for the time since the last call. O(1), no scheduler, no wasted wakeups on an idle
 * key. <br>
 * Rejected alternative: a {@code ScheduledExecutorService} ticking every key's bucket. That
 * is a live thread and a data structure entry per key even when the key is never touched
 * again — it does not scale to millions of idle keys, and it adds a second writer to
 * {@code tokens} for no benefit.
 *
 * <h2>What NOT to do</h2>
 * <ul>
 *   <li>Do not store {@code tokens} as an {@code int} and refill by whole tokens only.
 *       At a refill rate of 5/sec a 100ms call would credit {@code 0} (integer floor)
 *       forever, and the bucket never recovers between rapid calls. {@code double}
 *       accumulates the fractional 0.5 token so it is not silently lost.</li>
 *   <li>Do not skip the {@code Math.min(capacity, ...)} clamp. Without it an idle bucket
 *       accrues unbounded tokens and the very next burst can be far larger than
 *       {@code capacity} — the burst limit becomes meaningless.</li>
 *   <li>Do not update {@code lastRefillMillis} when no time has passed (or the clock went
 *       backwards). Advancing it on a zero/negative delta would swallow real elapsed time
 *       on the following call and starve the bucket.</li>
 * </ul>
 *
 * <h2>In AWS / distributed environment</h2>
 * {@code synchronized} here is a per-JVM lock — on a 4-pod service it gives four
 * independent buckets, so a client's real burst allowance is {@code 4 * capacity}. The
 * standard shared version stores {@code {tokens, lastRefillMillis}} as a Redis hash and
 * runs the read-refill-consume sequence inside one Lua script (ElastiCache), which Redis
 * executes atomically — no app-side lock, one bucket for the whole fleet.
 */
public final class TokenBucketRateLimiter implements RateLimiter {

    private final long capacity;
    private final double refillTokensPerMs;
    private final LongSupplier nowMillis;

    /** Fractional token count. Starts full so a fresh key gets its whole burst allowance. */
    private double tokens;
    /** Wall-clock millis of the last time {@link #refill()} actually credited tokens. */
    private long lastRefillMillis;

    /** Production constructor — real wall clock. */
    public TokenBucketRateLimiter(long capacity, double refillTokensPerSecond) {
        this(capacity, refillTokensPerSecond, System::currentTimeMillis);
    }

    /**
     * Test constructor — inject a fake clock so window boundaries can be hit exactly with
     * no {@code Thread.sleep} (deterministic, fast, no flakiness).
     */
    public TokenBucketRateLimiter(long capacity, double refillTokensPerSecond, LongSupplier nowMillis) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        if (refillTokensPerSecond <= 0) {
            throw new IllegalArgumentException("refillTokensPerSecond must be positive");
        }
        this.capacity = capacity;
        this.refillTokensPerMs = refillTokensPerSecond / 1000.0;
        this.nowMillis = nowMillis;
        this.tokens = capacity;
        this.lastRefillMillis = nowMillis.getAsLong();
    }

    // synchronized: refill() then the tokens>=1 check then the decrement must be one atomic
    // step. Without the lock, two threads both read tokens==1, both pass the check, both
    // decrement — the bucket goes negative and a call was allowed that should not have been.
    @Override
    public synchronized boolean limit() {
        refill();
        if (tokens >= 1.0) {
            tokens -= 1.0;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = nowMillis.getAsLong();
        long elapsed = now - lastRefillMillis;
        if (elapsed <= 0) {
            // No time passed, or a non-monotonic clock moved backwards. Credit nothing and
            // leave lastRefillMillis alone so the next positive delta is measured in full.
            return;
        }
        tokens = Math.min(capacity, tokens + elapsed * refillTokensPerMs);
        lastRefillMillis = now;
    }
}
