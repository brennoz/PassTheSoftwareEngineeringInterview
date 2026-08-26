package com.interview.algorithms.java_features.q063_highest_value_transaction;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

public class MyAnswer {

    public static Optional<Transaction> highestValueTransaction(List<Transaction> transactions) {
        return transactions.stream()
            .reduce(BinaryOperator.maxBy(Comparator.comparing(Transaction::amount)));
    }
}
