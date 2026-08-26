package com.interview.ood.q036_bank_service_layer.solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Pattern: Repository Adapter — in-memory implementation for tests and local dev.
// In prod: replaced by JpaTransferRepository backed by Postgres.
// Thread safety: HashMap not thread-safe. Fine for single-threaded tests.
// In prod multi-pod: Postgres provides isolation; two concurrent transfers on the same
//   account are serialised by row-level locks (SELECT FOR UPDATE on the balance row).
public class InMemoryTransferRepository implements TransferRepository {

    private final Map<String, Transfer> store = new HashMap<>();

    @Override
    public void save(Transfer transfer) {
        store.put(transfer.id(), transfer);
    }

    @Override
    public Optional<Transfer> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Transfer> findByAccountId(String accountId) {
        // A Transfer involves two accounts — match either side.
        List<Transfer> results = store.values().stream()
                .filter(t -> t.fromAccountId().equals(accountId)
                          || t.toAccountId().equals(accountId))
                .toList(); // Java 16+ — returns unmodifiable list
        return results;
    }
}
