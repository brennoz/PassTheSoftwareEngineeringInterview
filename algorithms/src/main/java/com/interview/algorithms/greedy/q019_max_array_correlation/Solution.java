package com.interview.algorithms.greedy.q019_max_array_correlation;

import java.util.Arrays;
import java.util.TreeMap;

public class Solution {

    // Greedy: sort b; for each a[i] (sorted asc) assign smallest b > a[i]
    // TreeMap gives O(log N) ceiling lookup
    // Time O(N log N) | Space O(N)
    public long getMaxArrayCorrelation(int[] a, int[] b) {
        Arrays.sort(a);
        TreeMap<Integer, Integer> freq = new TreeMap<>();
        for (int v : b) freq.merge(v, 1, Integer::sum);

        long correlation = 0;
        for (int ai : a) {
            Integer key = freq.higherKey(ai);
            if (key != null) {
                correlation += key;
                if (freq.merge(key, -1, Integer::sum) == 0) freq.remove(key);
            }
        }
        return correlation;
    }
}
