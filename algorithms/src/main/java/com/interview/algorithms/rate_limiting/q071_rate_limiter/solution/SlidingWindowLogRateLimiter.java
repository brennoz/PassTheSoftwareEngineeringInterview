package com.interview.algorithms.rate_limiting.q071_rate_limiter.solution;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.LongSupplier;

import com.interview.algorithms.rate_limiting.q071_rate_limiter.RateLimiter;

/**
 * Sliding Window Log.
 *
 * <p>Keep the timestamp of every allowed call. On each new call, drop the timestamps that
 * are now older than {@code windowMillis} and count what is left: if it is below
 * {@code maxRequests}, allow and record. The "window" is the last {@code windowMillis}
 * measured backwards from <i>now</i>, so it truly slides — there are no fixed edges to
 * straddle, which is exactly the boundary-spike bug Fixed Window has.
 *
 * <h2>Design decision — {@code ArrayDeque} of timestamps</h2>
 * Timestamps are appended in increasing order and evicted from the front, so a double-ended
 * queue gives O(1) {@code addLast} and O(1) {@code pollFirst}. The eviction loop is
 * amortised O(1) per call: each timestamp is added once and removed once over its lifetime.
 * <br>
 * Rejected alternatives: <br>
 * • {@code ArrayList} + {@code remove(0)} — {@code remove(0)} is O(n) because it shifts the
 *   whole backing array on every eviction. <br>
 * • {@code PriorityQueue} — pointless: insertion order already is timestamp order, so a heap
 *   just adds a log-factor and object overhead for nothing.
 *
 * <h2>The real cost — this is why it is not the default</h2>
 * Memory is O(maxRequests) <i>per key</i>: a limit of 10 000 req/min means up to 10 000
 * {@code Long} boxes held per active key. Fixed Window is two {@code long}s. Sliding Window
 * <i>Counter</i> (current-window count + previous-window count × overlap fraction) gets
 * ~99% of the accuracy for O(1) state and is what most systems actually ship; the Log is
 * the exact-but-expensive reference point.
 *
 * <h2>What NOT to do</h2>
 * <ul>
 *   <li>Do not record the timestamp <i>before</i> the size check — that counts the current
 *       call against itself and lets through one fewer than {@code maxRequests}.</li>
 *   <li>Do not use {@code &lt;} for eviction. An entry exactly {@code windowMillis} old is
 *       outside the half-open window {@code (now - windowMillis, now]} and must go, so the
 *       test is {@code timestamp &lt;= now - windowMillis}. Off-by-one here means a stale
 *       call keeps blocking a slot for one extra millisecond.</li>
 *   <li>Do not forget the eviction loop can leave the deque empty — {@code peekFirst()}
 *       returns {@code null} then; the {@code !isEmpty()} guard in the loop condition is
 *       load-bearing, not defensive noise.</li>
 * </ul>
 *
 * <h2>In AWS / distributed environment</h2>
 * Redis sorted set per key on ElastiCache: {@code ZREMRANGEBYSCORE key 0 (now-window)} to
 * evict, {@code ZCARD} to count, {@code ZADD key now now} to record, wrapped in one Lua
 * script so the sequence is atomic. Same O(maxRequests) memory problem, now in Redis RAM
 * and multiplied by the number of active keys — capacity-plan for it or use the Counter
 * variant.
 */
public final class SlidingWindowLogRateLimiter implements RateLimiter {

    private final int maxRequests;
    private final long windowMillis;
    private final LongSupplier nowMillis;

    /** Timestamps of allowed calls, oldest at the head. */
    private final Deque<Long> log = new ArrayDeque<>();

    /** Production constructor — real wall clock. */
    public SlidingWindowLogRateLimiter(int maxRequests, long windowMillis) {
        this(maxRequests, windowMillis, System::currentTimeMillis);
    }

    /** Test constructor — inject a fake clock (see {@link TokenBucketRateLimiter}). */
    public SlidingWindowLogRateLimiter(int maxRequests, long windowMillis, LongSupplier nowMillis) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("windowMillis must be positive");
        }
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.nowMillis = nowMillis;
    }

    // synchronized: eviction, the size check, and the append are one atomic step over a
    // shared non-thread-safe ArrayDeque. Concurrent unsynchronised access would also
    // corrupt the deque's internal indices, not just miscount.
    @Override
    public synchronized boolean limit() {
        long now = nowMillis.getAsLong();
        long cutoff = now - windowMillis;
        while (!log.isEmpty() && log.peekFirst() <= cutoff) {
            log.pollFirst();
        }
        if (log.size() < maxRequests) {
            log.addLast(now);
            return true;
        }
        return false;
    }
}
