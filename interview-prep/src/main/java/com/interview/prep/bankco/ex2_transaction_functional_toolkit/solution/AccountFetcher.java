package com.interview.prep.bankco.ex2_transaction_functional_toolkit.solution;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

// Stands in for a real async call — a DB lookup or a downstream account-service HTTP call.
// Returns CompletableFuture<Account> so callers must compose with it asynchronously rather
// than block — the whole point of the thenApply/thenCompose exercise below.
public class AccountFetcher {

    public CompletableFuture<Account> fetchAccount(String accountId) {
        return CompletableFuture.supplyAsync(() ->
                new Account(accountId, new BigDecimal("500.00"), true));
    }
}
