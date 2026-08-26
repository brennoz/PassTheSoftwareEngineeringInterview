package com.interview.algorithms.java_features.q055_top_spender_per_department.solution;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Purchase> purchases = List.of(
                new Purchase("Alice", "Engineering", new BigDecimal("300.00")),
                new Purchase("Bob", "Engineering", new BigDecimal("150.00")),
                new Purchase("Bob", "Engineering", new BigDecimal("200.00")), // Bob's total: 350 > Alice's 300
                new Purchase("Carol", "Sales", new BigDecimal("500.00"))
        );

        System.out.println("Functional:  " + Solution.topSpenderPerDepartment(purchases));
        System.out.println("Imperative:  " + ImperativeApproach.topSpenderPerDepartment(purchases));
        // Both print: {Engineering=Bob, Sales=Carol}
    }
}
