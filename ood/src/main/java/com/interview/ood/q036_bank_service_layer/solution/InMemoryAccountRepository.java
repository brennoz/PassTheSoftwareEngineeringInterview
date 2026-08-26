package com.interview.ood.q036_bank_service_layer.solution;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Pattern: Repository Adapter (in-memory implementation for tests and local dev).
// Implements AccountRepository (the Port) — swappable with JpaAccountRepository in prod.
//
// Two separate Maps: accounts (metadata) and balances (monetary state).
// Why separate? Balance changes far more frequently than account metadata.
// In prod (Postgres): separate columns on the same row, but balance is updated with
//   an atomic UPDATE ... SET balance = balance + ? to avoid lost-update races.
// In prod (Redis cache): balances could live in Redis (INCRBYFLOAT) for O(1) atomic ops,
//   with Postgres as the source of truth. Cache-aside: read from Redis, fall back to Postgres.
//   Multi-pod: all pods share the same Redis → no stale-balance problem across instances.
//   Contrast: if balance were a field on Account in a local HashMap, each pod has its own
//   copy → concurrent deposits on two pods produce wrong totals (split-brain).
//
// Thread safety: HashMap is NOT thread-safe. In a single-threaded test this is fine.
// In prod: replaced by Postgres (transactions handle isolation) or ConcurrentHashMap + AtomicReference
// for a genuinely in-memory multi-threaded scenario (e.g. embedded mode for demos).
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<String, Account>    accounts = new HashMap<>();
    private final Map<String, BigDecimal> balances = new HashMap<>();

    @Override
    public void save(Account account) {
        accounts.put(account.id(), account);
        // Initialise balance to zero only if this is a new account (don't overwrite on update).
        balances.putIfAbsent(account.id(), BigDecimal.ZERO);
    }

    @Override
    public Optional<Account> findById(String id) {
        return Optional.ofNullable(accounts.get(id));
    }

    @Override
    public BigDecimal addToBalance(String accountId, BigDecimal amount) {
        BigDecimal current = getBalance(accountId);
        BigDecimal updated = current.add(amount);
        balances.put(accountId, updated);
        return updated;
    }

    @Override
    public BigDecimal subtractFromBalance(String accountId, BigDecimal amount) {
        BigDecimal current = getBalance(accountId);
        // Guard lives here — the repository is the atomic unit, not the service.
        // In prod: Postgres CHECK constraint (balance >= 0) is an additional safety net.
        if (current.compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountId, amount, current);
        }
        BigDecimal updated = current.subtract(amount);
        balances.put(accountId, updated);
        return updated;
    }

    @Override
    public BigDecimal getBalance(String accountId) {
        return balances.getOrDefault(accountId, BigDecimal.ZERO);
    }
}
