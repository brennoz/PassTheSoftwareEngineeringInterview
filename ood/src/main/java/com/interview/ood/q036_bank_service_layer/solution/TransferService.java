package com.interview.ood.q036_bank_service_layer.solution;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// Pattern: Service Layer — orchestrates a cross-account transfer using AccountService.
// TransferService does NOT touch balance or ledger directly — delegates to AccountService.
// Single Responsibility: this class knows HOW to wire two account operations into one transfer.
//
// Operation order: withdraw first, deposit second.
// Why? If deposit ran first and withdraw then failed, the destination is credited for free.
// Withdraw first: if it throws InsufficientFundsException, deposit is never called — no phantom credit.
//
// In prod (@Transactional): both withdraw() and deposit() must commit atomically.
// If the JVM crashes between the two calls:
//   - withdraw succeeded (debit recorded) but deposit never ran → money vanishes.
//   @Transactional wraps the entire transfer() method → both commits or full rollback.
//
// In prod (distributed / cross-bank): @Transactional only covers one database.
// Cross-bank = Saga pattern:
//   1. Debit source     → publish TransferInitiated (Outbox)
//   2. Credit destination service consumes event → publishes TransferCompleted
//   3. On failure       → publish CompensationDebit (credit source back)
// Each step is idempotent: replaying the event produces the same result (UUID idempotency key).
//
// In prod (multi-pod race): two concurrent transfers from the same account.
// Both read balance = £200, both approve £150 withdrawal → overdraft.
// Fix: SELECT FOR UPDATE (Postgres row lock) or @Version optimistic lock on account row.
// subtractFromBalance in the repo is the atomic guard — but only within one transaction.
public class TransferService {

    private final AccountService     accountService;
    private final TransferRepository transferRepo;

    public TransferService(AccountService accountService, TransferRepository transferRepo) {
        this.accountService = accountService;
        this.transferRepo   = transferRepo;
    }

    public Transfer transfer(String fromId, String toId, BigDecimal amount) {
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account: " + fromId);
        }

        // Withdraw first — throws InsufficientFundsException if balance < amount.
        // If this throws, deposit is never called → no phantom credit on destination.
        accountService.withdraw(fromId, amount);

        // Deposit second — in prod, still inside the same @Transactional boundary.
        // If this throws (e.g. account not found), @Transactional rolls back the withdraw.
        accountService.deposit(toId, amount);

        Transfer transfer = new Transfer(
                UUID.randomUUID().toString(),
                fromId,
                toId,
                amount,
                Instant.now()
        );
        transferRepo.save(transfer);
        return transfer;
    }
}
