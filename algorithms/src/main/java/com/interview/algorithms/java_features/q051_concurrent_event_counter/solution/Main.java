package com.interview.algorithms.java_features.q051_concurrent_event_counter.solution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
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
            pool.awaitTermination(10, TimeUnit.SECONDS);
        }

        System.out.println("Expected: " + (threads * callsPerThread));
        System.out.println("Actual:   " + counter.countOf("checkout"));
    }
}
