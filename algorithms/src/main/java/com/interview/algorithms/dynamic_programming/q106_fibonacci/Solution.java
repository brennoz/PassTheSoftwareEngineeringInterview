package com.interview.algorithms.dynamic_programming.q106_fibonacci;

import java.math.BigInteger;

public class Solution {

    public enum FibonacciMode { IMPERATIVE, RECURSIVE, DYNAMIC }

    public BigInteger fibonacci(int n, FibonacciMode mode) {
        return switch (mode) {
            case IMPERATIVE -> fibImperative(n);
            case RECURSIVE  -> fibRecursive(n);
            case DYNAMIC    -> fibDynamic(n, new BigInteger[n + 1]);
        };
    }

    // O(n) O(1) — sliding window, no extra array
    private BigInteger fibImperative(int n) {
        if (n <= 1) return BigInteger.valueOf(n);
        BigInteger a = BigInteger.ZERO, b = BigInteger.ONE;
        for (int i = 2; i <= n; i++) { BigInteger next = a.add(b); a = b; b = next; }
        return b;
    }

    // O(2^n) — naive recursion; exponential, included for comparison only
    private BigInteger fibRecursive(int n) {
        if (n <= 1) return BigInteger.valueOf(n);
        return fibRecursive(n - 1).add(fibRecursive(n - 2));
    }

    // O(n) top-down memoization — computes each sub-problem once
    private BigInteger fibDynamic(int n, BigInteger[] memo) {
        if (n <= 1) return BigInteger.valueOf(n);
        if (memo[n] != null) return memo[n];
        memo[n] = fibDynamic(n - 1, memo).add(fibDynamic(n - 2, memo));
        return memo[n];
    }
}
