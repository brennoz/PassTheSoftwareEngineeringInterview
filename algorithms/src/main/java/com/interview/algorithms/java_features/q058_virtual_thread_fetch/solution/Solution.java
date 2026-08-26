package com.interview.algorithms.java_features.q058_virtual_thread_fetch.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Solution {

    // Virtual threads, not a fixed platform-thread pool: a platform thread is a real OS
    // thread — expensive to create, and a fixed pool caps how many blocking I/O calls can be
    // in flight at once regardless of how many tasks are actually waiting. A virtual thread is
    // cheap enough that "one per task" is fine even for thousands of tasks — the JVM schedules
    // many virtual threads onto a small number of underlying platform "carrier" threads, and a
    // virtual thread that's blocked on I/O doesn't tie up a carrier thread while it waits.
    // newVirtualThreadPerTaskExecutor() is built specifically for this "one task, one virtual
    // thread, task is I/O-bound" shape.
    //
    // try-with-resources on the ExecutorService: AutoCloseable since Java 19 — close() calls
    // shutdown() and blocks until in-flight tasks finish (equivalent to shutdown() +
    // awaitTermination()), so a caller can't accidentally leak the executor's threads by
    // forgetting to shut it down manually.
    //
    // Order preservation: submit() is called once per ID, in order, building a
    // List<Future<String>> whose index corresponds to customerIds' index. Future.get() BLOCKS
    // until that specific task completes — calling .get() on futures.get(0) waits for task 0
    // specifically, regardless of whether task 5 already finished. Iterating the futures list
    // in order and calling .get() on each therefore reconstructs input order even though the
    // underlying tasks completed in some other order — the Future object, not completion
    // timing, is what ties a result back to its original position.
    public static List<String> fetchAllConcurrently(List<String> customerIds, Function<String, String> fetchCustomerData)
            throws InterruptedException, ExecutionException {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new ArrayList<>();
            for (String id : customerIds) {
                futures.add(executor.submit(() -> fetchCustomerData.apply(id)));
            }

            List<String> results = new ArrayList<>();
            for (Future<String> future : futures) {
                results.add(future.get());
            }
            return results;
        }
    }
}
