package com.interview.algorithms.rate_limiting.q071_rate_limiter.solution;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.LongSupplier;

import org.junit.jupiter.api.Test;

/**
 * Token Bucket tests.
 *
 * <p>Time is a one-element {@code long[]} the test mutates directly; {@code NOW} reads it.
 * This replaces {@code Thread.sleep}: window boundaries are hit exactly, runs are
 * sub-millisecond, and there is no timing flakiness. A superficially similar test that used
 * {@code Thread.sleep(1000)} to "wait for a refill" would be both slow and unreliable — the
 * sleep can overshoot under load and the assertion then depends on the machine.
 */
class TokenBucketRateLimiterTest {

    private final long[] clock = {0L};
    private final LongSupplier now = () -> clock[0];

    @Test
    void allowsUpToCapacityThenRejects() {
        // Happy path + the one meaningful failure: a fresh bucket starts full, so exactly
        // `capacity` calls pass with no time advance, and the next one is throttled.
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(3, 1.0, now);

        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertFalse(limiter.limit(), "4th call with an empty bucket and no refill must be throttled");
    }

    @Test
    void refillsOverTime() {
        // Drain the bucket, then advance the clock by exactly one second. At 1 token/sec
        // that credits exactly one token — one call gets through, the next does not.
        // This is the core of the algorithm: budget returns as a function of elapsed time,
        // not of a fixed window rollover.
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(3, 1.0, now);
        limiter.limit();
        limiter.limit();
        limiter.limit();
        assertFalse(limiter.limit());

        clock[0] = 1000;

        assertTrue(limiter.limit(), "1s at 1 token/sec should have credited exactly one token");
        assertFalse(limiter.limit(), "only one token was credited, so the next call is throttled");
    }

    @Test
    void refillIsCappedAtCapacity() {
        // Idle far longer than it takes to "fill" the bucket (10s at 10 tokens/sec would be
        // 100 tokens) and confirm the burst is still clamped to `capacity`. A version
        // missing the Math.min clamp would allow far more than 3 here — this test is what
        // catches that specific bug.
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(3, 10.0, now);
        limiter.limit();
        limiter.limit();
        limiter.limit();

        clock[0] = 10_000;

        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertFalse(limiter.limit(), "accrued tokens must be clamped to capacity, not 100");
    }
}
