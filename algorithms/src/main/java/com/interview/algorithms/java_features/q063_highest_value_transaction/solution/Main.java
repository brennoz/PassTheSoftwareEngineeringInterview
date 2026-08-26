package com.interview.algorithms.java_features.q063_highest_value_transaction.solution;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Transaction> transactions = List.of(
                new Transaction("t1", new BigDecimal("120.00")),
                new Transaction("t2", new BigDecimal("980.50")),
                new Transaction("t3", new BigDecimal("45.00"))
        );

        System.out.println("Functional:  " + Solution.highestValueTransaction(transactions));
        System.out.println("Imperative:  " + ImperativeApproach.highestValueTransaction(transactions));

        System.out.println("Empty list, functional:  " + Solution.highestValueTransaction(List.of()));
        System.out.println("Empty list, imperative:  " + ImperativeApproach.highestValueTransaction(List.of()));
    }
}
