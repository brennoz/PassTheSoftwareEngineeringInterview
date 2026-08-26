package com.interview.algorithms.dynamic_programming.q011_maximum_subarray;

public class Solution {

    // Kadane's: extend current subarray or start fresh at current element
    public int maxSubArray(int[] nums) {
        int maxSum = nums[0];
        int current = nums[0];
        for (int i = 1; i < nums.length; i++) {
            current = Math.max(nums[i], current + nums[i]);
            maxSum = Math.max(maxSum, current);
        }
        return maxSum;
    }
}
