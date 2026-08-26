package com.interview.algorithms.arrays_and_hashing.q001_two_sum;

import java.util.HashMap;
import java.util.Map;

public class MyAnswer {

    public static void main(String[] args) {
        MyAnswer my = new MyAnswer();
        int[] results = my.twoSum(new int[]{2, 7, 11, 15}, 9);
        System.out.println(results[0] + " " + results[1]);
        results = my.twoSum(new int[]{3, 2, 4}, 6);
        System.out.println(results[0] + " " + results[1]);
    }

    public int[] twoSum(int[] nums, int target) {

        /**
            2   7   11  15
            {
                7 position 0
            }
            2 diff = 9 - 2 = 7
        */

        Map<Integer, Integer> diffMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int diff = target - nums[i];
            if (diffMap.containsKey(nums[i])) {
                return new int[]{diffMap.get(nums[i]), i};
            }
            diffMap.put(diff, i);
        }
        return new int[]{};
    }
}
