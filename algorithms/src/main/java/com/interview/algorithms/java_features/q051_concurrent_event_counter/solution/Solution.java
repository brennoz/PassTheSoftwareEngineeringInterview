package com.interview.algorithms.java_features.q051_concurrent_event_counter.solution;

import java.util.concurrent.ConcurrentHashMap;

public class Solution {

    private final ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();

    // merge() performs the read-combine-write as one atomic operation from the map's
    // perspective — no other thread's merge()/compute() on the SAME key can interleave
    // between "read the current value" and "write the new one". That's the whole fix: the
    // naive get()-then-put() version is racy not because ConcurrentHashMap is unsafe, but
    // because two separate method calls give another thread a window to run its own
    // get()-then-put() in between.
    //
    // What NOT to do: don't reach for `synchronized` here. Wrapping a ConcurrentHashMap's
    // own methods in your own lock works, but it throws away the whole point of using a
    // concurrent collection — you're back to single-threaded access to the map, just with
    // extra ceremony. The map already offers atomic per-key operations; use those.
    public void record(String event) {
        counts.merge(event, 1, Integer::sum);
    }

    public int countOf(String event) {
        return counts.getOrDefault(event, 0);
    }
}
