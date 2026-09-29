package com.interview.algorithms.java_features.q067_partitioned_payment_totals.solution;

import com.interview.algorithms.java_features.q067_partitioned_payment_totals.MyAnswer.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ImperativeApproach {

    // Two accumulators — one loop. Straightforward, but verbose.
    // Both keys are seeded to ZERO before the loop, matching partitioningBy's guarantee
    // that both keys are always present even when a partition is empty.
    // The functional version expresses this guarantee structurally (partitioningBy does it
    // for you); here you have to think about it yourself and seed accordingly.
    public static Map<Boolean, BigDecimal> totalByOutcome(List<Transaction> transactions) {
        BigDecimal successTotal = BigDecimal.ZERO;
        BigDecimal failureTotal = BigDecimal.ZERO;

        for (Transaction tx : transactions) {
            if (tx.successful()) {
                successTotal = successTotal.add(tx.amount());
            } else {
                failureTotal = failureTotal.add(tx.amount());
            }
        }

        return Map.of(true, successTotal, false, failureTotal);
    }
}
