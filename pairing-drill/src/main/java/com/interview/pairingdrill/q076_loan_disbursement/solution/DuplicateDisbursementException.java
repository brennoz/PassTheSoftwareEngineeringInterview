package com.interview.pairingdrill.q076_loan_disbursement.solution;

public class DuplicateDisbursementException extends RuntimeException {

    public DuplicateDisbursementException(Long loanAccountId) {
        super("Loan account " + loanAccountId + " is already being processed — refusing duplicate disbursement");
    }
}
