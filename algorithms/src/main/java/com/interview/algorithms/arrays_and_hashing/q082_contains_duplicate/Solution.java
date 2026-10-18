package com.interview.algorithms.arrays_and_hashing.q082_contains_duplicate;

import java.util.HashSet;

public class Solution {

    // O(n) time, O(n) space — HashSet membership check
    boolean hasDuplicate(int[] nums) {
        var seen = new HashSet<Integer>();
        for (int num : nums) {
            if (!seen.add(num)) {
                return true;
            }
        }
        return false;
    }
}
