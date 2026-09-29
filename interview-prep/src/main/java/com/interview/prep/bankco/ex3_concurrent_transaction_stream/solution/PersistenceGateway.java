package com.interview.prep.bankco.ex3_concurrent_transaction_stream.solution;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

// Stands in for an async downstream write (audit log, Kafka publish, DB insert). Returns
// CompletableFuture<Void> — StreamMain composes it with thenCompose after the (synchronous,
// lock-protected) ledger update, so "update balance" and "persist the result" are two
// distinct async-composable steps rather than one blocking method doing both.
public class PersistenceGateway {

    private final Queue<String> persisted = new ConcurrentLinkedQueue<>();

    public CompletableFuture<Void> persist(String accountId, BigDecimal resultingBalance) {
        return CompletableFuture.runAsync(() ->
                persisted.add(accountId + "=" + resultingBalance));
    }

    public int persistedCount() {
        return persisted.size();
    }
}
