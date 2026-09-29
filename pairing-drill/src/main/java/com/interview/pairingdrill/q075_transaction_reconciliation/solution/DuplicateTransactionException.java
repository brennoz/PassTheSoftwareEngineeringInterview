package com.interview.pairingdrill.q075_transaction_reconciliation.solution;

// Why this exists: the seeded code's default Collectors.toMap (no merge function) already
// threw on a duplicate ID — so the crash wasn't new, just uninformative (a bare
// IllegalStateException: Duplicate key, no transaction ID in the message, easy to mistake for
// a different bug entirely). A duplicate transaction ID reaching this method points at a
// missing idempotency guarantee upstream, wherever transactions are published from — failing
// loudly with the actual ID is the safe default until that's fixed, not silently summing or
// picking one (which would risk double-counting real money).
public class DuplicateTransactionException extends RuntimeException {

    public DuplicateTransactionException(String transactionId) {
        super("Duplicate transaction ID reached reconciliation: " + transactionId);
    }
}
