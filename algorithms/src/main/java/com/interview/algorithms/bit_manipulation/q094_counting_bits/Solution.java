package com.interview.algorithms.bit_manipulation.q094_counting_bits;

public class Solution {

    // O(n) DP — dp[i] = 1 + dp[i - offset]; offset doubles when it equals i
    public int[] countBits(int n) {
        int[] dp = new int[n + 1];
        int offset = 1;
        for (int i = 1; i <= n; i++) {
            if (offset * 2 == i) offset = i;
            dp[i] = 1 + dp[i - offset];
        }
        return dp;
    }
}
