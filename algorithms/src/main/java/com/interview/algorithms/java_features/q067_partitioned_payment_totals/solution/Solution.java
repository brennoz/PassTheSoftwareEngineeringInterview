package com.interview.algorithms.java_features.q067_partitioned_payment_totals.solution;

import com.interview.algorithms.java_features.q067_partitioned_payment_totals.MyAnswer.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    // partitioningBy vs groupingBy(t -> t.successful()): both cluster by a boolean, but
    // partitioningBy ALWAYS returns both true and false keys — even when a partition is empty.
    // groupingBy omits keys that have no matching elements, so if all transactions succeed,
    // groupingBy would return {true -> [...]} with no false key at all.
    // The finance report needs both totals unconditionally — partitioningBy is the correct tool.
    //
    // reducing(identity, mapper, combiner) — the 3-arg form:
    // - Returns BigDecimal directly (identity seeds empty partitions to BigDecimal.ZERO).
    // - The 1-arg form (BinaryOperator only) returns Optional<BigDecimal> — wrong return type.
    //
    // Why not summingDouble: floating-point loses precision for monetary values.
    // No summingBigDecimal exists in the standard library — reducing is the correct workaround.
    public static Map<Boolean, BigDecimal> totalByOutcome(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.partitioningBy(
                        Transaction::successful,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::amount, BigDecimal::add)
                ));
    }
}
