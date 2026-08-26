package com.interview.algorithms.sliding_window.q009_best_time_buy_sell_stock;

public class MyAnswer {

    public static void main (String[] args) {
        MyAnswer myAnswer = new MyAnswer();
        System.out.println("Max profit for these days: " + myAnswer.maxProfit(new int[] {7,1,5,3,6,4}));
        System.out.println("Max profit for these days: " + myAnswer.maxProfit(new int[] {7,6,4,3,1}));
        System.out.println("Max profit for these days: " + myAnswer.maxProfit(new int[] {1,2}));
    }

    public int maxProfit(int[] prices) {
        int l = 0;
        int maxProfit = 0;
        /*
            7   1   5   3   6   4
                L               R
            1 < 7 -> No
         */
        int profit = 0;
        for (int r = 1; r < prices.length; r++) {
            if (prices[r] < prices[l]) {
                l = r;
            } else {
                profit = (prices[r] - prices[l]);
                maxProfit = Math.max(profit, maxProfit);
            }
        }
        return maxProfit;
    }
}
