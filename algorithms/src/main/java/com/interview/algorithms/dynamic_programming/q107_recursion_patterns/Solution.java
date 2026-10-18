package com.interview.algorithms.dynamic_programming.q107_recursion_patterns;

public class Solution {

    // Pre-order print → recurse → post-order (backtracking) print
    public void head(int n) {
        System.out.printf("Calling with n=%s%n", n);
        if (n == 0) return;
        head(n - 1);
        System.out.printf("(Backtracking) n=%s%n", n);
    }
}
