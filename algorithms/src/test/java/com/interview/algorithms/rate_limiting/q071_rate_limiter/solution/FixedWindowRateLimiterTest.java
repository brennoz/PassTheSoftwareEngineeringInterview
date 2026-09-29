package com.interview.algorithms.rate_limiting.q071_rate_limiter.solution;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.LongSupplier;

import org.junit.jupiter.api.Test;

/**
 * Fixed Window Counter tests. Fake clock, same rationale as {@link TokenBucketRateLimiterTest}.
 *
 * <p>The fourth test ({@code boundarySpike...}) is not an edge case to be defended against —
 * it documents the algorithm's known weakness on purpose, so the trade-off against Sliding
 * Window is concrete and not just a sentence in a comment.
 */
class FixedWindowRateLimiterTest {

    private final long[] clock = {0L};
    private final LongSupplier now = () -> clock[0];

    @Test
    void allowsMaxRequestsPerWindowThenRejects() {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(3, 1000, now);

        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertFalse(limiter.limit(), "4th call in the same window must be throttled");
    }

    @Test
    void counterResetsWhenWindowAdvances() {
        // Fill window [0,1000), then step to t=1000 which is window [1000,2000) — a fresh
        // budget. Proves the reset is tied to the window rollover.
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(3, 1000, now);
        limiter.limit();
        limiter.limit();
        limiter.limit();
        assertFalse(limiter.limit());

        clock[0] = 1000;

        assertTrue(limiter.limit(), "new window → counter reset");
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertFalse(limiter.limit());
    }

    @Test
    void counterDoesNotResetWithinTheSameWindow() {
        // Advance time but stay inside window 0 (t=500, windowMillis=1000). A naive
        // "reset if time since last call > windowMillis" implementation could wrongly
        // refill here; the correct window-id logic does not.
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(3, 1000, now);
        limiter.limit();
        limiter.limit();
        limiter.limit();

        clock[0] = 500;

        assertFalse(limiter.limit(), "still window 0 — no reset mid-window");
    }

    @Test
    void boundarySpikeAllowsDoubleAcrossAdjacentWindows() {
        // The flaw: maxRequests in the last ms of window 0, then maxRequests in the first
        // ms of window 1 → 2 * maxRequests calls allowed within a 1ms span. This is correct
        // behaviour for THIS algorithm and the reason Sliding Window exists.
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(3, 1000, now);

        clock[0] = 999;
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        assertFalse(limiter.limit());

        clock[0] = 1000;
        assertTrue(limiter.limit(), "new window opens the gate again 1ms later");
        assertTrue(limiter.limit());
        assertTrue(limiter.limit());
        // 6 calls allowed between t=999 and t=1000 — double the nominal limit of 3.
    }
}
