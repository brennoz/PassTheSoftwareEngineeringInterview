package com.interview.pairingdrill.q075_transaction_reconciliation.answer;


public class DuplicateTransactionException extends RuntimeException {

    public DuplicateTransactionException(String transactionId) {
        super("Duplicate transaction ID reached reconciliation: " + transactionId);
    }
}
