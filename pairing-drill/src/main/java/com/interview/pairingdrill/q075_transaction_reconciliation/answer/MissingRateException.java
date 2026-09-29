package com.interview.pairingdrill.q075_transaction_reconciliation.answer;

public class MissingRateException extends RuntimeException {

    public MissingRateException(String currencyCode) {
        super("No exchange rate available for currency: " + currencyCode);
    }
}
