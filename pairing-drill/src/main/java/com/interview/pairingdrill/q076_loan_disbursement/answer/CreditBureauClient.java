package com.interview.pairingdrill.q076_loan_disbursement.answer;

public interface CreditBureauClient {

    // Synchronous call to an external credit bureau. Nothing in this interface or its callers
    // bounds how long this can take.
    int checkCreditScore(String customerId);
}
