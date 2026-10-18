package com.interview.algorithms.arrays_and_hashing.q085_product_of_array_except_self;

public class Solution {

    // O(n) time, O(1) extra space — prefix pass fills result, postfix pass multiplies in-place
    public int[] solve(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        res[0] = 1;
        for (int i = 1; i < n; i++) {
            res[i] = res[i - 1] * nums[i - 1];
        }
        int postfix = 1;
        for (int i = n - 1; i >= 0; i--) {
            res[i] *= postfix;
            postfix *= nums[i];
        }
        return res;
    }
}
