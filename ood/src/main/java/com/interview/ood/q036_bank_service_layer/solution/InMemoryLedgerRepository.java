package com.interview.ood.q036_bank_service_layer.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Pattern: Repository Adapter (in-memory append-only ledger).
// ArrayList per accountId — O(1) append, O(N) full history read.
// In prod this is fine; for point-in-time queries we use balanceAfter on Transaction directly.
//
// Collections.unmodifiableList: history() returns a view that throws on any mutation attempt.
// Callers receive a read-only snapshot — they cannot accidentally clear the ledger.
// In prod (Postgres): the DB role has INSERT-only privilege; SELECT returns rows sorted by createdAt.
// In prod (Kafka Streams): history could be materialised as a KTable (account_id → List<Transaction>)
//   for real-time aggregation without hitting Postgres on every read.
public class InMemoryLedgerRepository implements LedgerRepository {

    // accountId → ordered list of transactions (insertion order = chronological order)
    private final Map<String, List<Transaction>> ledger = new HashMap<>();

    @Override
    public void record(Transaction transaction) {
        // computeIfAbsent: creates the list only once on first transaction for this account.
        ledger.computeIfAbsent(transaction.accountId(), id -> new ArrayList<>())
              .add(transaction);
    }

    @Override
    public List<Transaction> history(String accountId) {
        List<Transaction> entries = ledger.getOrDefault(accountId, List.of());
        // Return unmodifiable — callers see the history but cannot corrupt it.
        return Collections.unmodifiableList(entries);
    }
}
