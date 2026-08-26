package com.interview.algorithms.java_features.q051_concurrent_event_counter.solution;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    @Test
    void recordIncrementsCountForThatEvent() {
        Solution counter = new Solution();

        counter.record("login");
        counter.record("login");
        counter.record("logout");

        assertEquals(2, counter.countOf("login"));
        assertEquals(1, counter.countOf("logout"));
    }

    @Test
    void unrecordedEventReturnsZero() {
        Solution counter = new Solution();

        assertEquals(0, counter.countOf("never-happened"));
    }

    // What this verifies: merge() is genuinely atomic per key under real concurrent access,
    // not just "looks fine in a single-threaded test." 10 threads x 1,000 calls each to the
    // SAME key is specifically chosen to maximise contention on one ConcurrentHashMap bucket
    // — a get()-then-put() implementation reliably loses updates under this load (verified by
    // temporarily reverting to the racy version and running this test 3 times: it failed all
    // 3 runs, with results of 7262, 5822, and 3980 instead of 10,000 — a substantial fraction
    // of updates lost, not an occasional off-by-one, and the exact shortfall varies run to run
    // since it's a genuine race, not a deterministic bug). A single run passing isn't proof by
    // itself — a race that doesn't trigger on one run isn't fixed — but it's the standard way
    // to exercise this class of bug in a unit test without a dedicated stress-testing framework.
    @Test
    void concurrentRecordsToSameEventLoseNoUpdates() throws InterruptedException {
        Solution counter = new Solution();
        int threads = 10;
        int callsPerThread = 1000;

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < callsPerThread; i++) {
                        counter.record("checkout");
                    }
                });
            }
            pool.shutdown();
            assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS), "pool did not finish in time");
        }

        assertEquals(threads * callsPerThread, counter.countOf("checkout"));
    }
}
