package com.interview.algorithms.binary_search.q092_find_min_rotated_sorted_array;

public class Solution {

    // O(log n) — if nums[l] < nums[r] the subarray is sorted; min is nums[l]
    // Otherwise mid is in the left (larger) portion → search right
    public int findMin(int[] nums) {
        int l = 0, r = nums.length - 1;
        int res = nums[0];
        while (l <= r) {
            if (nums[l] < nums[r]) {
                res = Math.min(res, nums[l]);
                break;
            }
            int m = l + (r - l) / 2;
            res = Math.min(res, nums[m]);
            if (nums[m] >= nums[l]) {
                l = m + 1;
            } else {
                r = m - 1;
            }
        }
        return res;
    }
}
