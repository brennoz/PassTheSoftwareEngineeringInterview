package com.interview.pairingdrill.q075_transaction_reconciliation.solution;

// Why this exists: the seeded code let a missing rate surface as a bare NullPointerException
// from inside Optional.of(null) — technically a crash, but not one that tells anyone what
// actually went wrong. A named exception makes "we don't have a rate for this currency" a
// distinguishable, catchable condition instead of an NPE indistinguishable from any other bug.
public class MissingRateException extends RuntimeException {

    public MissingRateException(String currencyCode) {
        super("No exchange rate available for currency: " + currencyCode);
    }
}
