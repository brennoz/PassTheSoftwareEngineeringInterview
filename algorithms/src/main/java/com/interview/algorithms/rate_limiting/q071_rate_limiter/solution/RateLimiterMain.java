package com.interview.algorithms.rate_limiting.q071_rate_limiter.solution;

import java.util.function.LongSupplier;

import com.interview.algorithms.rate_limiting.q071_rate_limiter.RateLimiter;

/**
 * Runnable demo — run from the IDE: right-click → Run 'RateLimiterMain.main()'.
 *
 * <p>Drives all three limiters through the <b>same</b> sequence of calls against a shared
 * fake clock, so the difference you see is purely the algorithm. No {@code Thread.sleep} —
 * time is a {@code long} we move by hand, which is also how the tests do it.
 *
 * <p>Config for every limiter: capacity / maxRequests = 5, window = 1000 ms
 * (token bucket refills 5 tokens/sec, i.e. one full window's worth per second).
 */
public final class RateLimiterMain {

    /** Mutable clock: {@code clock[0]} is "now" in millis; the LongSupplier just reads it. */
    private static final long[] CLOCK = {0L};
    private static final LongSupplier NOW = () -> CLOCK[0];

    public static void main(String[] args) {
        scenarioA_identicalBurst();
        System.out.println();
        scenarioB_fixedWindowBoundarySpike();
    }

    /**
     * Scenario A — fire 7 calls at t=0, then 5 more at t=200ms, then 5 more at t=1000ms.
     * Expected shape:
     *   t=0     all three allow 5, reject 2
     *   t=200   token bucket has refilled 1 token (5/sec × 0.2s) → allows 1; the two
     *           window-based limiters reject all
     *   t=1000  token bucket credits 4 more (5/sec × 0.8s since the t=200 refill) → allows
     *           4, not 5, because the t=200 call already advanced its refill clock; fixed
     *           window rolled to a new slot → allows 5; sliding log's t=0 entries are now
     *           exactly 1000ms old and get evicted → allows 5
     */
    private static void scenarioA_identicalBurst() {
        CLOCK[0] = 0;
        RateLimiter tokenBucket = new TokenBucketRateLimiter(5, 5.0, NOW);
        RateLimiter fixedWindow = new FixedWindowRateLimiter(5, 1000, NOW);
        RateLimiter slidingLog = new SlidingWindowLogRateLimiter(5, 1000, NOW);

        System.out.println("=== Scenario A: identical burst against all three ===");
        System.out.printf("%-8s %-6s  %-14s %-14s %-18s%n",
                "call", "t(ms)", "TokenBucket", "FixedWindow", "SlidingWindowLog");

        int call = 0;
        for (int i = 0; i < 7; i++) {
            printRow(++call, tokenBucket, fixedWindow, slidingLog);
        }
        CLOCK[0] = 200;
        for (int i = 0; i < 5; i++) {
            printRow(++call, tokenBucket, fixedWindow, slidingLog);
        }
        CLOCK[0] = 1000;
        for (int i = 0; i < 5; i++) {
            printRow(++call, tokenBucket, fixedWindow, slidingLog);
        }
    }

    /**
     * Scenario B — the Fixed Window boundary spike, in isolation, next to Sliding Log for
     * contrast. Fill the limit in the last ms of one window, then again in the first ms of
     * the next: Fixed Window lets 10 through in 1ms, Sliding Log holds the line at 5.
     */
    private static void scenarioB_fixedWindowBoundarySpike() {
        RateLimiter fixedWindow = new FixedWindowRateLimiter(5, 1000, NOW);
        RateLimiter slidingLog = new SlidingWindowLogRateLimiter(5, 1000, NOW);

        System.out.println("=== Scenario B: boundary spike (t=999 then t=1000) ===");
        System.out.printf("%-8s %-6s  %-14s %-18s%n", "call", "t(ms)", "FixedWindow", "SlidingWindowLog");

        int call = 0;
        CLOCK[0] = 999;
        for (int i = 0; i < 5; i++) {
            System.out.printf("%-8d %-6d  %-14s %-18s%n",
                    ++call, CLOCK[0], verdict(fixedWindow.limit()), verdict(slidingLog.limit()));
        }
        CLOCK[0] = 1000;
        for (int i = 0; i < 5; i++) {
            System.out.printf("%-8d %-6d  %-14s %-18s%n",
                    ++call, CLOCK[0], verdict(fixedWindow.limit()), verdict(slidingLog.limit()));
        }
        System.out.println("FixedWindow allowed 10 calls within 1ms; SlidingWindowLog allowed 5.");
    }

    private static void printRow(int call, RateLimiter tb, RateLimiter fw, RateLimiter sl) {
        System.out.printf("%-8d %-6d  %-14s %-14s %-18s%n",
                call, CLOCK[0], verdict(tb.limit()), verdict(fw.limit()), verdict(sl.limit()));
    }

    private static String verdict(boolean allowed) {
        return allowed ? "ALLOW" : "reject";
    }
}
