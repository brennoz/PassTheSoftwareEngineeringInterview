package com.interview.algorithms.bit_manipulation.q096_reverse_bits;

public class Solution {

    // O(32) — extract bit i from n, place it at position (31-i) in result
    public int reverseBits(int n) {
        int res = 0;
        for (int i = 0; i < 32; i++) {
            int bit = (n >> i) & 1;
            res += (bit << (31 - i));
        }
        return res;
    }
}
