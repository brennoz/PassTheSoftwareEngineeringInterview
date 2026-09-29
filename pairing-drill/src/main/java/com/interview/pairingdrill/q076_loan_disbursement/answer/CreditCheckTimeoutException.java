package com.interview.pairingdrill.q076_loan_disbursement.answer;

public class CreditCheckTimeoutException extends RuntimeException {

    public CreditCheckTimeoutException(String customerId) {
        super("Credit bureau check timed out for customer: " + customerId);
    }
}
