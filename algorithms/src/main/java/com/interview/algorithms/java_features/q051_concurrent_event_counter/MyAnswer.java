package com.interview.algorithms.java_features.q051_concurrent_event_counter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyAnswer {
    // your implementation here
    //Enable HashMap for testing without synchronized in the record method signature and the test willl fail!!!
    //private final Map<String, Integer> counts = new HashMap<>();
    private final Map<String, Integer> counts = new ConcurrentHashMap<>();
    //private ReentrantLock lock = new ReentrantLock();

    public void record(String event) {
        //lock.lock();
        counts.merge(event, 1, Integer::sum);
        //lock.unlock();
    }

    public int countOf(String event) {
        return counts.getOrDefault(event, 0);
    }
}
