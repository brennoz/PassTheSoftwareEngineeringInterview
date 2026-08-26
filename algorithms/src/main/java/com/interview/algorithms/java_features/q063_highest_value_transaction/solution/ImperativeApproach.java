package com.interview.algorithms.java_features.q063_highest_value_transaction.solution;

import java.util.List;
import java.util.Optional;

// Trade-off vs Solution.java: "no elements yet" has to be represented by hand here — `highest`
// starts null, and every iteration needs a null-check before the first real comparison can
// happen. reduce()'s Optional-returning overload gets that same "might not have a result"
// state for free from the type signature, no null involved anywhere in the loop body.
public class ImperativeApproach {

    public static Optional<Transaction> highestValueTransaction(List<Transaction> transactions) {
        Transaction highest = null;
        for (Transaction transaction : transactions) {
            if (highest == null || transaction.amount().compareTo(highest.amount()) > 0) {
                highest = transaction;
            }
        }
        return Optional.ofNullable(highest);
    }
}
