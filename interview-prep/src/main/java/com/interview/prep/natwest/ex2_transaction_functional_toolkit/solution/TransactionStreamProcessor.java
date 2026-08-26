package com.interview.prep.natwest.ex2_transaction_functional_toolkit.solution;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.math.BigDecimal;

// RxJava Observable — a push-based, composable stream, same operator vocabulary as
// java.util.stream (map/filter/reduce) but async and capable of emitting 0..N values over
// TIME rather than pulling from an already-in-memory collection. Here the "stream" is a
// finite Observable for simplicity, but the same operators apply unchanged to a live,
// unbounded source (e.g. a websocket of incoming transactions).
public class TransactionStreamProcessor {

    private static final BigDecimal MAX_SINGLE_TRANSACTION = new BigDecimal("10000.00");

    // filter: drop anything non-positive or over the single-transaction limit (fraud rule).
    // reduce(seed, accumulator): fold every remaining element into one running total —
    // functionally identical shape to Stream.reduce, just over an async source.
    public Single<BigDecimal> totalOfValidTransactions(Observable<BigDecimal> amounts) {
        return amounts
                .filter(amount -> amount.compareTo(BigDecimal.ZERO) > 0)
                .filter(amount -> amount.compareTo(MAX_SINGLE_TRANSACTION) <= 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // map: pure 1-to-1 transform, no filtering — every emitted amount becomes a formatted
    // "charged" string. Kept separate from totalOfValidTransactions to show map and
    // filter/reduce as distinct, composable operators rather than one monolithic pipeline.
    public Observable<String> describeEach(Observable<BigDecimal> amounts) {
        return amounts.map(amount -> "charged: " + amount);
    }
}
