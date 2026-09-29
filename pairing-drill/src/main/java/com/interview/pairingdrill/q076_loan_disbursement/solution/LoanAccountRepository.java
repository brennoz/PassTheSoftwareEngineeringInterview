package com.interview.pairingdrill.q076_loan_disbursement.solution;

public interface LoanAccountRepository {

    LoanAccount findById(Long id);

    void save(LoanAccount account);

    // Why this exists: the seeded code went straight from findById to mutating status with no
    // gate in between — two concurrent disburseLoan calls for the same loan (a client retry
    // being the realistic trigger) could both read the same PENDING account and both proceed,
    // producing two ledger entries and paying out twice. This is the atomic compare-and-set a
    // real JPA @Version column enforces at the database level: succeeds only if the stored
    // version still matches what the caller read; the loser gets false, not a corrupted write.
    boolean claimForProcessing(Long id, long expectedVersion);
}
