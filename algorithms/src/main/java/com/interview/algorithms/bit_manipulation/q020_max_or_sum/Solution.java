package com.interview.algorithms.bit_manipulation.q020_max_or_sum;

public class Solution {

    // Greedy: always double the element that sets the most NEW high bits
    // Apply all k doublings to the same best candidate — spreading doubles across
    // elements never beats concentrating them (OR is monotone, doubling one elem
    // shifts its bits left without disturbing bits already set by others)
    // Time O(N * k) | Space O(1)
    public long getMaxOrSum(int[] arr, int k) {
        long baseOr = 0;
        for (int v : arr) baseOr |= v;

        long best = 0;
        for (int i = 0; i < arr.length; i++) {
            long candidate = arr[i];
            long orWithout = baseOr & ~arr[i]; // OR of all elements except arr[i]
            for (int op = 0; op < k; op++) candidate *= 2;
            best = Math.max(best, orWithout | candidate);
        }
        return best;
    }
}
