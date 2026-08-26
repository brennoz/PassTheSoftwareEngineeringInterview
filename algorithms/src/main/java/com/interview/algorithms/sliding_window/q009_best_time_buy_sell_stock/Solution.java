package com.interview.algorithms.sliding_window.q009_best_time_buy_sell_stock;

public class Solution {

    // Track min price seen so far; max profit = max(profit, price - minSoFar)
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;
        for (int price : prices) {
            if (price < minPrice) {
                minPrice = price;
            } else {
                maxProfit = Math.max(maxProfit, price - minPrice);
            }
        }
        return maxProfit;
    }
}
