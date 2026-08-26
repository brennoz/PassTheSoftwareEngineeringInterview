package com.interview.algorithms.java_features.q058_virtual_thread_fetch.solution;

import java.util.List;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) throws Exception {
        List<String> customerIds = List.of("c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10");

        Function<String, String> slowFetch = id -> {
            try {
                Thread.sleep(100); // simulate a slow I/O call, e.g. a network request
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "data-for-" + id;
        };

        long sequentialStart = System.currentTimeMillis();
        List<String> sequentialResults = ImperativeApproach.fetchAllConcurrently(customerIds, slowFetch);
        long sequentialMillis = System.currentTimeMillis() - sequentialStart;

        long concurrentStart = System.currentTimeMillis();
        List<String> concurrentResults = Solution.fetchAllConcurrently(customerIds, slowFetch);
        long concurrentMillis = System.currentTimeMillis() - concurrentStart;

        System.out.println("Sequential (imperative): " + sequentialMillis + "ms  -> " + sequentialResults);
        System.out.println("Concurrent (virtual threads): " + concurrentMillis + "ms  -> " + concurrentResults);
        System.out.println("Same results, same order: " + sequentialResults.equals(concurrentResults));
        // Sequential: ~1000ms (10 x 100ms, one after another)
        // Concurrent: ~100-150ms (all 10 fetches running at once on virtual threads)
    }
}
