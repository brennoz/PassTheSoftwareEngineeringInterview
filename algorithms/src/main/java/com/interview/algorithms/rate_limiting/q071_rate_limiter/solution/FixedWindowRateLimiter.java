package com.interview.algorithms.rate_limiting.q071_rate_limiter.solution;

import java.util.function.LongSupplier;

import com.interview.algorithms.rate_limiting.q071_rate_limiter.RateLimiter;

/**
 * Fixed Window Counter.
 *
 * <p>Chop time into fixed slots of {@code windowMillis} (…, [0,1000), [1000,2000), …) and
 * allow at most {@code maxRequests} per slot. State is two numbers — the current window id
 * and a counter — so it is the cheapest limiter to run and the easiest to explain.
 *
 * <h2>Design decision — epoch-aligned windows, not "window since first request"</h2>
 * The window id is {@code now / windowMillis}. Every key rolls over at the same absolute
 * instants, which makes the behaviour predictable and matches how people reason about it
 * ("100 requests per minute" = per clock minute). <br>
 * Rejected alternative: start the window at the first request and roll over
 * {@code windowMillis} later ("sliding start"). That turns into a weak per-key token bucket
 * without the burst smoothing, and two keys created 1ms apart have windows that never
 * align — harder to test, harder to reason about, no upside.
 *
 * <h2>Known flaw this implementation deliberately keeps — the boundary spike</h2>
 * A client can send {@code maxRequests} in the last millisecond of window N and another
 * {@code maxRequests} in the first millisecond of window N+1: {@code 2 * maxRequests} calls
 * inside a span shorter than one window, even though every individual window is within
 * limit. {@code FixedWindowRateLimiterTest#boundarySpikeAllowsDoubleAcrossAdjacentWindows}
 * pins this down. Sliding Window Log / Counter exist precisely to remove it — the trade is
 * O(1) state here vs. O(maxRequests) timestamps (log) or a second counter (counter).
 *
 * <h2>What NOT to do</h2>
 * <ul>
 *   <li>Do not reset the counter based on "time since the last request" — that is a
 *       different algorithm. The reset is tied to the absolute window id; a call at
 *       {@code t=500} with {@code windowMillis=1000} is still in window 0 no matter when
 *       the previous call landed.</li>
 *   <li>Do not use {@code HashMap<Long,Integer>} keyed by window id to "keep history".
 *       Only the current window is ever consulted; old entries are pure leak.</li>
 * </ul>
 *
 * <h2>In AWS / distributed environment</h2>
 * The canonical distributed limiter: {@code INCR rate:{key}:{windowId}} then
 * {@code EXPIRE} it for {@code windowMillis} on ElastiCache (Redis). {@code INCR} is atomic
 * and returns the post-increment value, so the check-then-act race is gone. The boundary
 * spike survives the port unchanged — it is a property of the algorithm, not of where the
 * counter lives.
 */
public final class FixedWindowRateLimiter implements RateLimiter {

    private final int maxRequests;
    private final long windowMillis;
    private final LongSupplier nowMillis;

    /** Absolute window id ({@code now / windowMillis}) that {@link #count} belongs to. */
    private long currentWindowId;
    private int count;

    /** Production constructor — real wall clock. */
    public FixedWindowRateLimiter(int maxRequests, long windowMillis) {
        this(maxRequests, windowMillis, System::currentTimeMillis);
    }

    /** Test constructor — inject a fake clock (see {@link TokenBucketRateLimiter}). */
    public FixedWindowRateLimiter(int maxRequests, long windowMillis, LongSupplier nowMillis) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("windowMillis must be positive");
        }
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.nowMillis = nowMillis;
        this.currentWindowId = nowMillis.getAsLong() / windowMillis;
        this.count = 0;
    }

    // synchronized: the rollover check, the counter read, and the increment are one atomic
    // step. Without the lock two threads at count == maxRequests-1 both see room and both
    // increment to maxRequests+1 — one request over the limit slipped through.
    @Override
    public synchronized boolean limit() {
        long windowId = nowMillis.getAsLong() / windowMillis;
        if (windowId != currentWindowId) {
            currentWindowId = windowId;
            count = 0;
        }
        if (count < maxRequests) {
            count++;
            return true;
        }
        return false;
    }
}
