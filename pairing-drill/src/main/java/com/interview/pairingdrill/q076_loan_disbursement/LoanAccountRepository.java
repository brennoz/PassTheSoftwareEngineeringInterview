package com.interview.pairingdrill.q076_loan_disbursement;

public interface LoanAccountRepository {

    LoanAccount findById(Long id);

    void save(LoanAccount account);
}
