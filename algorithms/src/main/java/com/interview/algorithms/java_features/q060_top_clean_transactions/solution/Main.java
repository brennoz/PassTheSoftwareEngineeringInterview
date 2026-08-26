package com.interview.algorithms.java_features.q060_top_clean_transactions.solution;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Transaction> transactions = List.of(
                new Transaction("t1", new BigDecimal("9000.00"), true),   // flagged, excluded
                new Transaction("t2", new BigDecimal("450.00"), false),
                new Transaction("t3", new BigDecimal("1200.00"), false),
                new Transaction("t4", new BigDecimal("1200.00"), false),  // ties t3 on amount
                new Transaction("t5", new BigDecimal("75.00"), false)
        );

        List<Transaction> top3 = Solution.topCleanTransactions(transactions, 3);
        System.out.println("Functional top 3: " + top3);

        List<Transaction> top3Imperative = ImperativeApproach.topCleanTransactions(transactions, 3);
        System.out.println("Imperative top 3: " + top3Imperative);
        System.out.println("Same result: " + top3.equals(top3Imperative));
    }
}
