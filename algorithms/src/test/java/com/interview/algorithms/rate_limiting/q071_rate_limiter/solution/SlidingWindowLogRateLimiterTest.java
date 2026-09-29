package com.interview.algorithms.rate_limiting.q071_rate_limiter.solution;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.LongSupplier;

import org.junit.jupiter.api.Test;

/**
 * Sliding Window Log tests. Fake clock, same rationale as {@link TokenBucketRateLimiterTest}.
 */
class SlidingWindowLogRateLimiterTest {

    private final long[] clock = {0L};
    private final LongSupplier now = () -> clock[0];

    @Test
    void allowsMaxRequestsInWindowThenRejects() {
        SlidingWindowLogRateLimiter limiter = new SlidingWindowLogRateLimiter(3, 1000, now);

        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertFalse(limiter.limit(), "4th call inside the window must be throttled");
    }

    @Test
    void evictsEntriesExactlyOneWindowOld() {
        // Three calls at t=0. At t=999 they are still inside the (now-1000, now] window, so
        // a 4th is rejected. At t=1000 the t=0 entries are exactly 1000ms old — outside the
        // half-open window — and are evicted, freeing all three slots at once.
        // The `<=` boundary in the limiter is what makes t=1000 (not t=1001) the release
        // point; this test pins that off-by-one.
        SlidingWindowLogRateLimiter limiter = new SlidingWindowLogRateLimiter(3, 1000, now);
        limiter.limit();
        limiter.limit();
        limiter.limit();

        clock[0] = 999;
        assertFalse(limiter.limit(), "entries from t=0 are still within the last 1000ms");

        clock[0] = 1000;
        assertTrue(limiter.limit(), "t=0 entries are now exactly one window old → evicted");
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
    }

    @Test
    void hasNoBoundarySpike() {
        // The direct contrast with FixedWindowRateLimiterTest#boundarySpike...: fill the
        // limit at t=500, then probe at t=1400. A fixed-window limiter would have reset at
        // t=1000 and allowed a fresh batch. The sliding log still sees 3 entries only
        // 900ms old, so it holds the line — no burst at the (non-existent) boundary.
        SlidingWindowLogRateLimiter limiter = new SlidingWindowLogRateLimiter(3, 1000, now);

        clock[0] = 500;
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());

        clock[0] = 1400;
        assertFalse(limiter.limit(), "entries at t=500 are 900ms old — still counted, no spike");
    }
}
