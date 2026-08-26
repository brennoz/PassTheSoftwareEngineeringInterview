package com.interview.algorithms.java_features.q063_highest_value_transaction.solution;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

public class Solution {

    // reduce(BinaryOperator<T>) — the no-identity overload — returns Optional<T>, not T. The
    // 2-arg overload (T identity, BinaryOperator<T> accumulator) needs a real starting value to
    // seed the fold; there's no meaningful "zero transaction" to hand it (an all-zero-amount
    // sentinel would work by luck here, but would be actively wrong for a MIN-based reduce, or
    // for any T that isn't already a "biggest wins" comparison). Returning Optional forces the
    // caller to handle "the input list was empty" explicitly, which a fabricated identity value
    // would silently paper over instead — an empty list would reduce to the fake sentinel and
    // look like a real (wrong) transaction to any caller that isn't careful.
    //
    // What NOT to do: `transactions.get(0)` as a manual seed, then loop comparing the rest —
    // that's just reduce() rewritten by hand, minus the type-level guarantee that "no elements"
    // is representable at all (Optional.empty()) rather than an ArrayIndexOutOfBoundsException
    // waiting to happen on `get(0)`.
    public static Optional<Transaction> highestValueTransaction(List<Transaction> transactions) {
        BinaryOperator<Transaction> keepHigherAmount = BinaryOperator.maxBy(Comparator.comparing(Transaction::amount));
        return transactions.stream().reduce(keepHigherAmount);
    }
}
