package com.interview.algorithms.bit_manipulation.q095_number_of_one_bits;

public class Solution {

    // O(1) — n & (n-1) strips the lowest set bit each iteration; loops exactly popcount(n) times
    public int hammingWeight(int n) {
        int res = 0;
        while (n != 0) {
            n &= n - 1;
            res++;
        }
        return res;
    }
}
