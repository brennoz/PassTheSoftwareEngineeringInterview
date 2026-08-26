package com.interview.algorithms.java_features.q058_virtual_thread_fetch.solution;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    // What this verifies: results come back in INPUT order, not completion order. Item at
    // index 0 is rigged to sleep the LONGEST (so it finishes LAST) and item at the highest
    // index sleeps the SHORTEST (finishes FIRST) — if the implementation collected results as
    // each task completed rather than by iterating Futures in submission order, this test
    // would see the results scrambled (highest-index items first).
    @Test
    void resultsPreserveInputOrderEvenWhenLaterItemsFinishFirst() throws Exception {
        List<String> ids = List.of("0", "1", "2", "3", "4");

        Function<String, String> reverseSpeedFetch = id -> {
            int index = Integer.parseInt(id);
            try {
                Thread.sleep((4 - index) * 30L); // index 0 sleeps longest, index 4 sleeps ~0
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "data-" + id;
        };

        List<String> results = Solution.fetchAllConcurrently(ids, reverseSpeedFetch);

        assertEquals(List.of("data-0", "data-1", "data-2", "data-3", "data-4"), results);
    }

    @Test
    void everyFetchRunsOnAVirtualThread() throws Exception {
        List<String> ids = List.of("a", "b", "c", "d", "e");
        List<Boolean> observedVirtualFlags = Collections.synchronizedList(new ArrayList<>());

        Function<String, String> fetch = id -> {
            observedVirtualFlags.add(Thread.currentThread().isVirtual());
            return "data-" + id;
        };

        Solution.fetchAllConcurrently(ids, fetch);

        assertEquals(5, observedVirtualFlags.size());
        assertTrue(observedVirtualFlags.stream().allMatch(Boolean::booleanValue));
    }

    // What this verifies: the fetches genuinely overlap in time rather than running one after
    // another. 10 fetches x 100ms each would take ~1000ms sequentially; run concurrently on
    // virtual threads, total wall-clock should be close to a single fetch's duration, not the
    // sum. The 500ms threshold leaves generous margin for scheduling overhead/CI slowness while
    // still being far below the ~1000ms a sequential implementation would take.
    @Test
    void fetchesRunConcurrentlyNotSequentially() throws Exception {
        List<String> ids = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        Function<String, String> slowFetch = id -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "data-" + id;
        };

        long start = System.currentTimeMillis();
        Solution.fetchAllConcurrently(ids, slowFetch);
        long elapsedMillis = System.currentTimeMillis() - start;

        assertTrue(elapsedMillis < 500, "expected well under 500ms, was " + elapsedMillis + "ms");
    }

    @Test
    void functionalAndImperativeApproachesAgreeOnResults() throws Exception {
        List<String> ids = List.of("x", "y", "z");
        Function<String, String> fetch = String::toUpperCase;

        assertEquals(
                ImperativeApproach.fetchAllConcurrently(ids, fetch),
                Solution.fetchAllConcurrently(ids, fetch)
        );
    }
}
