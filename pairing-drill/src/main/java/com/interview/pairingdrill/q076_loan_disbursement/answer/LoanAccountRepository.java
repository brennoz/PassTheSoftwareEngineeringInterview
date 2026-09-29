package com.interview.pairingdrill.q076_loan_disbursement.answer;

public interface LoanAccountRepository {

    LoanAccount findById(Long id);

    void save(LoanAccount account);

    boolean checkBeforeProcessing(Long id, long expectedVersion);
}
