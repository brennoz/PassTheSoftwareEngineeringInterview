package com.interview.ood.q036_bank_service_layer.solution;

import java.math.BigDecimal;

// ─── Wiring (what Spring would do automatically in prod) ──────────────────────
//
// In prod (Spring Boot):
//   @Bean InMemoryAccountRepository  → replaced by JpaAccountRepository (Postgres)
//   @Bean InMemoryLedgerRepository   → replaced by JpaLedgerRepository  (Postgres, append-only)
//   @Bean InMemoryTransferRepository → replaced by JpaTransferRepository (Postgres)
//   @Bean AccountService(accountRepo, ledgerRepo)
//   @Bean TransferService(accountService, transferRepo)
//   @Bean BankService(accountService, transferService)
//
// In prod (Redis cache): AccountRepository wraps JpaAccountRepository with a Redis layer.
//   Read: check Redis first (key "account:{id}"), fall back to Postgres, populate cache.
//   Write: write to Postgres, evict Redis key. TTL 5 min as safety net.
//   Multi-pod: all pods share one Redis cluster (ElastiCache) — no stale-balance split.
//
// In prod (Kafka): each service method publishes a domain event via Outbox:
//   AccountOpened → "bank.accounts" topic
//   MoneyDeposited / MoneyWithdrawn → "bank.transactions" topic (partitioned by accountId)
//   TransferCompleted → "bank.transfers" topic
//   Consumers: fraud detection (sliding-window anomaly), notification service, reporting.
//
// In prod (AWS):
//   RDS Aurora Multi-AZ (Postgres) — automatic failover, read replicas for reporting queries
//   ElastiCache Redis — balance cache, session store
//   MSK (Managed Kafka) — event streaming
//   SQS FIFO — DLQ for failed Kafka consumer messages (e.g. InsufficientFundsException)
//   CloudWatch / X-Ray — distributed tracing across service calls

public class Main {

    public static void main(String[] args) {

        // ── Wire the object graph manually (Spring does this via IoC container in prod) ──
        AccountRepository  accountRepo  = new InMemoryAccountRepository();
        LedgerRepository   ledgerRepo   = new InMemoryLedgerRepository();
        TransferRepository transferRepo = new InMemoryTransferRepository();

        AccountService  accountService  = new AccountService(accountRepo, ledgerRepo);
        TransferService transferService = new TransferService(accountService, transferRepo);
        BankService     bank            = new BankService(accountService, transferService);

        // ── Scenario: two customers, multiple accounts, transfers, close ──────────

        // Register customers (no-op here; in prod: KYC check + CustomerRegistered event)
        bank.register("roberto");
        bank.register("alice");

        // Open accounts
        Account robertoCurrent = bank.openAccount("roberto", AccountType.CURRENT);
        Account robertoSavings = bank.openAccount("roberto", AccountType.SAVINGS);
        Account aliceCurrent   = bank.openAccount("alice",   AccountType.CURRENT);

        System.out.println("=== Accounts opened ===");
        System.out.println("Roberto CURRENT : " + robertoCurrent.id());
        System.out.println("Roberto SAVINGS : " + robertoSavings.id());
        System.out.println("Alice   CURRENT : " + aliceCurrent.id());

        // ── Deposits ─────────────────────────────────────────────────────────────
        // BigDecimal("2000.00") — string constructor guarantees exact decimal representation.
        // Never BigDecimal(2000.0) — double constructor can introduce floating-point noise.
        bank.deposit(robertoCurrent.id(), new BigDecimal("2000.00"));
        bank.deposit(robertoSavings.id(), new BigDecimal("500.00"));
        bank.deposit(aliceCurrent.id(),   new BigDecimal("1000.00"));

        System.out.println("\n=== After deposits ===");
        System.out.println("Roberto CURRENT balance : £" + bank.getBalance(robertoCurrent.id()));
        System.out.println("Roberto SAVINGS balance : £" + bank.getBalance(robertoSavings.id()));
        System.out.println("Alice   CURRENT balance : £" + bank.getBalance(aliceCurrent.id()));

        // ── Withdrawal ───────────────────────────────────────────────────────────
        bank.withdraw(robertoCurrent.id(), new BigDecimal("300.00"));
        System.out.println("\n=== After Roberto withdraws £300 ===");
        System.out.println("Roberto CURRENT balance : £" + bank.getBalance(robertoCurrent.id()));

        // ── Transfer: Roberto → Alice ─────────────────────────────────────────────
        // TransferService: withdraw(roberto) first, then deposit(alice).
        // Both happen atomically in prod via @Transactional.
        // In prod (Outbox): TransferCompleted event published to Kafka after DB commit.
        Transfer transfer = bank.transfer(robertoCurrent.id(), aliceCurrent.id(), new BigDecimal("400.00"));
        System.out.println("\n=== After Roberto transfers £400 to Alice ===");
        System.out.println("Transfer id             : " + transfer.id());
        System.out.println("Roberto CURRENT balance : £" + bank.getBalance(robertoCurrent.id()));
        System.out.println("Alice   CURRENT balance : £" + bank.getBalance(aliceCurrent.id()));

        // ── Insufficient funds guard ──────────────────────────────────────────────
        System.out.println("\n=== InsufficientFundsException demo ===");
        try {
            bank.withdraw(robertoCurrent.id(), new BigDecimal("99999.00"));
        } catch (InsufficientFundsException e) {
            System.out.println("Caught (expected): " + e.getMessage());
            // In prod: @ControllerAdvice maps this to HTTP 422.
            // In Kafka consumer: this message goes to DLQ — no retry (deterministic failure).
        }

        // ── Ledger history ───────────────────────────────────────────────────────
        System.out.println("\n=== Roberto CURRENT ledger history ===");
        bank.history(robertoCurrent.id()).forEach(tx ->
            System.out.printf("  [%s] %s £%s  → balance £%s%n",
                tx.createdAt(), tx.type(), tx.amount(), tx.balanceAfter())
        );

        // ── Close Roberto's savings account (zero balance) ───────────────────────
        System.out.println("\n=== Closing Roberto's SAVINGS account ===");
        // Must drain it first
        bank.withdraw(robertoSavings.id(), new BigDecimal("500.00"));
        bank.closeAccount(robertoSavings.id());
        System.out.println("Roberto SAVINGS status  : " + accountRepo.findById(robertoSavings.id())
                .map(a -> a.status().name()).orElse("NOT FOUND"));

        // ── Cannot close account with remaining balance ───────────────────────────
        System.out.println("\n=== Cannot close Roberto's CURRENT (balance > 0) ===");
        try {
            bank.closeAccount(robertoCurrent.id());
        } catch (IllegalStateException e) {
            System.out.println("Caught (expected): " + e.getMessage());
        }

        System.out.println("\n=== Final balances ===");
        System.out.println("Roberto CURRENT : £" + bank.getBalance(robertoCurrent.id()));
        System.out.println("Alice   CURRENT : £" + bank.getBalance(aliceCurrent.id()));
    }
}
