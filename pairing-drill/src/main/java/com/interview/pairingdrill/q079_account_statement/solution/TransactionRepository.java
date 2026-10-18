package com.interview.pairingdrill.q079_account_statement.solution;

import java.util.List;

public interface TransactionRepository {
    List<Transaction> findByAccountId(String accountId);
    // Fix 5: bulk fetch used in generateBatch — was present in the seeded interface but never called.
    // The seeded generateBatch called findByAccountId inside a for-loop: O(N) repo calls.
    // findByAccountIds replaces that with one call, loading all transactions in a single query.
    List<Transaction> findByAccountIds(List<String> accountIds);
}
