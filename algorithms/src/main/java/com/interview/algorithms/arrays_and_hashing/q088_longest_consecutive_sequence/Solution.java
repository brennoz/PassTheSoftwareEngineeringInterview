package com.interview.algorithms.arrays_and_hashing.q088_longest_consecutive_sequence;

import java.util.HashSet;
import java.util.Set;

public class Solution {

    // O(n) — HashSet enables O(1) neighbour lookup; start-gate ensures each element counted once
    public int solve(int[] nums) {
        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) numSet.add(num);
        int longest = 0;
        for (int num : numSet) {
            if (!numSet.contains(num - 1)) {
                int length = 1;
                while (numSet.contains(num + length)) length++;
                longest = Math.max(longest, length);
            }
        }
        return longest;
    }
}
