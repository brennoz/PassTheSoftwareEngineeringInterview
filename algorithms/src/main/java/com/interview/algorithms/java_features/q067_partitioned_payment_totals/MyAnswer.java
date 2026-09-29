package com.interview.algorithms.java_features.q067_partitioned_payment_totals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyAnswer {

    public record Transaction(String id, BigDecimal amount, boolean successful) {}

    public static Map<Boolean, BigDecimal> totalByOutcome(List<Transaction> transactions) {
        return transactions.stream()
                    .collect(
                        Collectors.partitioningBy(Transaction::successful,
                                                  Collectors.reducing(BigDecimal.ZERO, Transaction::amount, BigDecimal::add))
                    );
    }
}
