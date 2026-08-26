package com.interview.algorithms.two_pointers.q029_three_sum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Algorithm: Sort + Two Pointers — O(N²) time, O(1) space (ignoring output)
// Pattern: fix outer index i, converge L and R for the remaining pair
public class Solution {

    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicate outer values
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            // Early exit: smallest triple already positive
            if (nums[i] > 0) break;

            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (sum == 0) {
                    result.add(List.of(nums[i], nums[l], nums[r]));
                    // Skip duplicates on both sides before moving inward
                    while (l < r && nums[l] == nums[l + 1]) l++;
                    while (l < r && nums[r] == nums[r - 1]) r--;
                    l++; r--;
                } else if (sum < 0) {
                    l++;
                } else {
                    r--;
                }
            }
        }
        return result;
    }
}
