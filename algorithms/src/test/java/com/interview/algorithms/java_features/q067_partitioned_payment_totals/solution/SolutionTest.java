package com.interview.algorithms.java_features.q067_partitioned_payment_totals.solution;

import com.interview.algorithms.java_features.q067_partitioned_payment_totals.MyAnswer.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    @Test
    void splits_totals_when_both_partitions_non_empty() {
        List<Transaction> txs = List.of(
                new Transaction("t1", new BigDecimal("100.00"), true),
                new Transaction("t2", new BigDecimal("250.50"), true),
                new Transaction("t3", new BigDecimal("75.25"), false),
                new Transaction("t4", new BigDecimal("30.00"), false)
        );

        Map<Boolean, BigDecimal> result = Solution.totalByOutcome(txs);

        // true partition: 100.00 + 250.50 = 350.50
        assertEquals(0, result.get(true).compareTo(new BigDecimal("350.50")));
        // false partition: 75.25 + 30.00 = 105.25
        assertEquals(0, result.get(false).compareTo(new BigDecimal("105.25")));
    }

    @Test
    void false_key_present_and_zero_when_all_transactions_succeed() {
        // This is the key distinction from groupingBy — false key must exist even with no
        // failed transactions. groupingBy would omit false entirely; partitioningBy cannot.
        List<Transaction> txs = List.of(
                new Transaction("t1", new BigDecimal("500.00"), true),
                new Transaction("t2", new BigDecimal("200.00"), true)
        );

        Map<Boolean, BigDecimal> result = Solution.totalByOutcome(txs);

        assertEquals(0, result.get(true).compareTo(new BigDecimal("700.00")));
        assertEquals(0, result.get(false).compareTo(BigDecimal.ZERO));
    }

    @Test
    void both_keys_zero_for_empty_list() {
        Map<Boolean, BigDecimal> result = Solution.totalByOutcome(List.of());

        assertEquals(0, result.get(true).compareTo(BigDecimal.ZERO));
        assertEquals(0, result.get(false).compareTo(BigDecimal.ZERO));
    }
}
