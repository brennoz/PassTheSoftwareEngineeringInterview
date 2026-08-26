package com.interview.algorithms.java_features.q063_highest_value_transaction.solution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    // What this verifies: the winner isn't just "whichever one reduce() saw last" — the
    // highest-amount transaction sits in the MIDDLE of the input list here, which rules out a
    // reduce accumulator that silently returns its second argument unconditionally (a common
    // way to get this "accidentally right" against a dataset where the winner happens to be
    // last).
    @Test
    void picksTheTransactionWithTheHighestAmount() {
        List<Transaction> transactions = List.of(
                new Transaction("t1", new BigDecimal("120.00")),
                new Transaction("t2", new BigDecimal("980.50")),
                new Transaction("t3", new BigDecimal("45.00"))
        );

        Optional<Transaction> result = Solution.highestValueTransaction(transactions);

        assertEquals("t2", result.orElseThrow().id());
    }

    // What this verifies: an empty input produces Optional.empty(), not a thrown exception and
    // not a fabricated placeholder Transaction — this is exactly the case the no-identity
    // reduce() overload exists to make representable without a null check.
    @Test
    void emptyListProducesEmptyOptional() {
        Optional<Transaction> result = Solution.highestValueTransaction(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void functionalAndImperativeApproachesAgreeOnResults() {
        List<Transaction> transactions = List.of(
                new Transaction("t1", new BigDecimal("120.00")),
                new Transaction("t2", new BigDecimal("980.50")),
                new Transaction("t3", new BigDecimal("45.00"))
        );

        assertEquals(
                ImperativeApproach.highestValueTransaction(transactions),
                Solution.highestValueTransaction(transactions)
        );
    }
}
