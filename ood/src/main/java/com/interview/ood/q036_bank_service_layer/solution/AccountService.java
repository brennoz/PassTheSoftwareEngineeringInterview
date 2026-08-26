package com.interview.ood.q036_bank_service_layer.solution;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

// Pattern: Service Layer — owns all business logic for account lifecycle.
// Account, Transaction records are dumb data containers; this class is where decisions happen.
//
// Why constructor injection (not @Autowired field injection)?
//   1. Dependencies are explicit — you cannot construct AccountService without its repos.
//   2. final fields → immutable after construction → thread-safe.
//   3. Testable without Spring: new AccountService(mockRepo, mockLedger) in unit tests.
//   4. Field injection hides dependencies; constructor injection makes the contract visible.
//
// @Transactional (in prod): every public method here would carry @Transactional.
//   deposit/withdraw: two writes (balance + ledger) must commit atomically.
//   Without @Transactional: balance updated → JVM crash → ledger entry missing → audit gap.
//   In Spring: @Transactional wraps the method in a Proxy; the real bean is injected, not `this`.
//   Calling open() from within this class bypasses the proxy → no transaction. Always inject self
//   or restructure to avoid internal calls across @Transactional boundaries.
//
// In prod (multi-pod / ECS): this service runs on N identical pods sharing one Postgres.
//   Race condition example: two pods both read balance = £100, both approve £80 withdraw.
//   Fix: subtractFromBalance uses Postgres row-level lock (SELECT FOR UPDATE) or
//   @Version optimistic locking on the account row → one succeeds, one gets OptimisticLockException.
//
// In prod (Kafka): after each successful deposit/withdraw, an event is published via Outbox:
//   MoneyDeposited / MoneyWithdrawn → consumed by fraud detection, notification service, reporting.
public class AccountService {

    private final AccountRepository accountRepo;
    private final LedgerRepository  ledgerRepo;

    // Constructor injection — dependencies are final, thread-safe, explicitly declared.
    public AccountService(AccountRepository accountRepo, LedgerRepository ledgerRepo) {
        this.accountRepo = accountRepo;
        this.ledgerRepo  = ledgerRepo;
    }

    // Creates and persists a new ACTIVE account.
    // UUID: statistically unique across all pods with no coordination required.
    //   In prod: could use Snowflake ID (timestamp + machine id + sequence) for k-sortable IDs
    //   that are also roughly chronological — better for DB index locality (avoids page splits).
    public Account open(String ownerId, AccountType type) {
        Account account = new Account(UUID.randomUUID().toString(), ownerId, type, AccountStatus.ACTIVE);
        accountRepo.save(account);
        return account;
    }

    // Credits the account and records an immutable CREDIT entry in the ledger.
    // Two writes → must be @Transactional in prod so both commit or both roll back.
    public Transaction deposit(String accountId, BigDecimal amount) {
        accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        BigDecimal balanceAfter = accountRepo.addToBalance(accountId, amount);

        Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                accountId,
                TransactionType.CREDIT,
                amount,
                balanceAfter,
                Instant.now()
        );
        ledgerRepo.record(tx);
        return tx;
    }

    // Debits the account. subtractFromBalance throws InsufficientFundsException on overdraft.
    // ledgerRepo.record() is intentionally called AFTER a successful subtract —
    // if the subtract throws, no phantom DEBIT appears in the audit log.
    public Transaction withdraw(String accountId, BigDecimal amount) {
        accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        BigDecimal balanceAfter = accountRepo.subtractFromBalance(accountId, amount);

        Transaction tx = new Transaction(
                UUID.randomUUID().toString(),
                accountId,
                TransactionType.DEBIT,
                amount,
                balanceAfter,
                Instant.now()
        );
        ledgerRepo.record(tx);
        return tx;
    }

    public BigDecimal getBalance(String accountId) {
        return accountRepo.getBalance(accountId);
    }

    // Delegates to the ledger — AccountService is the single entry point for all account ops.
    // In prod: paginated; full history could be millions of rows. Returns read-only view.
    public List<Transaction> history(String accountId) {
        return ledgerRepo.history(accountId);
    }

    // Closes the account. Rejects if balance > 0 — customer still owns that money.
    // Uses withStatus() to produce a new immutable Account record (no setters on records).
    // In prod: @Transactional — read balance + update status must be atomic to prevent
    //   a concurrent deposit slipping in between the balance check and the status update.
    public void close(String accountId) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        BigDecimal balance = accountRepo.getBalance(accountId);
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException(
                    "Cannot close account " + accountId + " with remaining balance: " + balance);
        }

        accountRepo.save(account.withStatus(AccountStatus.CLOSED));
    }
}
