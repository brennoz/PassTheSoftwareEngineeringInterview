package com.interview.algorithms.java_features.q054_suppressed_exceptions.solution;

public class Main {

    public static void main(String[] args) {
        System.out.println("Neither throws on close:");
        Solution.describeFailure(false, false).forEach(System.out::println);

        System.out.println();
        System.out.println("Both throw on close:");
        Solution.describeFailure(true, true).forEach(System.out::println);
        // primary: body failed
        // suppressed: close failed: B   <- B closes first (declared second, LIFO)
        // suppressed: close failed: A
    }
}
