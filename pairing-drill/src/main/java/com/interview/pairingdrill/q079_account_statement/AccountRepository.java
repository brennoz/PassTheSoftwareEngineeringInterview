package com.interview.pairingdrill.q079_account_statement;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(String accountId);
    List<Account> findAllById(List<String> accountIds);
}
