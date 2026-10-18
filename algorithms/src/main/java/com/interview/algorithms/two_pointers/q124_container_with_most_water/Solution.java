package com.interview.algorithms.two_pointers.q124_container_with_most_water;

public class Solution {

    // Algorithm: converging two pointers; move the shorter wall inward — it's the only move that can improve area
    public int solve(int[] heights) {
        int l = 0, r = heights.length - 1, res = 0;
        while (l < r) {
            res = Math.max(res, Math.min(heights[l], heights[r]) * (r - l));
            if (heights[l] <= heights[r]) {
                l++;
            } else {
                r--;
            }
        }
        return res;
    }
}
