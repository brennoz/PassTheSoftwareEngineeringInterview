package com.interview.algorithms.dynamic_programming.q011_maximum_subarray;

public class MyAnswer {

    public static void main (String[] args) {
        MyAnswer my = new MyAnswer();
        System.out.println(my.maxSubArray(new int[] {-2,1,-3,4,-1,2,1,-5,4}));
    }

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
