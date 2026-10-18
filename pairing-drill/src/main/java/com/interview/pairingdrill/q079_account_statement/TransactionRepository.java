package com.interview.pairingdrill.q079_account_statement;

import java.util.List;

public interface TransactionRepository {
    List<Transaction> findByAccountId(String accountId);
    List<Transaction> findByAccountIds(List<String> accountIds);
}
