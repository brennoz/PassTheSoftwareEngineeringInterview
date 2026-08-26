package com.interview.algorithms.java_features.q060_top_clean_transactions.solution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    // What this verifies: flagged transactions never appear in the result, even when they'd
    // otherwise be the highest-amount entries — partitioningBy must actually exclude them, not
    // just deprioritize them.
    @Test
    void flaggedTransactionsAreExcludedEvenWhenHighestAmount() {
        List<Transaction> transactions = List.of(
                new Transaction("t1", new BigDecimal("9000.00"), true),
                new Transaction("t2", new BigDecimal("450.00"), false),
                new Transaction("t3", new BigDecimal("75.00"), false)
        );

        List<Transaction> result = Solution.topCleanTransactions(transactions, 3);

        assertEquals(List.of("t2", "t3"), result.stream().map(Transaction::id).toList());
    }

    // What this verifies: result is ordered by amount descending, and correctly truncated to
    // topN even when more clean transactions exist than requested.
    @Test
    void resultIsSortedDescendingByAmountAndCappedAtTopN() {
        List<Transaction> transactions = List.of(
                new Transaction("t1", new BigDecimal("100.00"), false),
                new Transaction("t2", new BigDecimal("500.00"), false),
                new Transaction("t3", new BigDecimal("300.00"), false),
                new Transaction("t4", new BigDecimal("50.00"), false)
        );

        List<Transaction> result = Solution.topCleanTransactions(transactions, 2);

        assertEquals(List.of("t2", "t3"), result.stream().map(Transaction::id).toList());
    }

    // What this verifies: two transactions with an identical amount get a deterministic
    // relative order (by id) rather than relying on incidental stream/sort stability — a
    // superficially-similar test using distinct amounts throughout would never exercise this
    // tiebreaker path at all.
    @Test
    void tiedAmountsBreakDeterministicallyById() {
        List<Transaction> transactions = List.of(
                new Transaction("b", new BigDecimal("1200.00"), false),
                new Transaction("a", new BigDecimal("1200.00"), false)
        );

        List<Transaction> result = Solution.topCleanTransactions(transactions, 2);

        assertEquals(List.of("a", "b"), result.stream().map(Transaction::id).toList());
    }

    @Test
    void functionalAndImperativeApproachesAgreeOnResults() {
        List<Transaction> transactions = List.of(
                new Transaction("t1", new BigDecimal("9000.00"), true),
                new Transaction("t2", new BigDecimal("450.00"), false),
                new Transaction("t3", new BigDecimal("1200.00"), false),
                new Transaction("t4", new BigDecimal("1200.00"), false),
                new Transaction("t5", new BigDecimal("75.00"), false)
        );

        assertEquals(
                ImperativeApproach.topCleanTransactions(transactions, 3),
                Solution.topCleanTransactions(transactions, 3)
        );
    }
}
