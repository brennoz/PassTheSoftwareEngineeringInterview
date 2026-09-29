package com.interview.pairingdrill.q076_loan_disbursement.solution;

public class CreditCheckTimeoutException extends RuntimeException {

    public CreditCheckTimeoutException(String customerId) {
        super("Credit bureau check timed out for customer: " + customerId);
    }
}
