package com.interview.ood.q036_bank_service_layer.solution;

import java.math.BigDecimal;
import java.time.Instant;

// Pattern: Thin Data Container — records the fact that a transfer happened between two accounts.
// A Transfer produces TWO Transactions: one DEBIT on fromAccountId, one CREDIT on toAccountId.
// Double-entry bookkeeping: sum of all credits == sum of all debits across the whole ledger.
//
// In prod (@Transactional boundary): TransferService must write both Transaction rows AND the
//   Transfer row in a single database transaction. If the CREDIT write fails, the DEBIT rolls back.
//   Without this guarantee, money disappears (debit recorded, credit lost).
//
// In prod (Saga / distributed): if fromAccount and toAccount live in different microservices
//   (e.g. cross-bank transfer), a 2-phase commit is impractical. Use a Saga:
//   1. Debit fromAccount → publish TransferInitiated event
//   2. Credit toAccount  → publish TransferCompleted event
//   3. On failure        → publish CompensationDebit (credit fromAccount back)
//   Outbox pattern ensures the event is published exactly once even if the service crashes
//   between DB commit and Kafka publish.
//
// In prod (idempotency): the Transfer id (UUID) doubles as an idempotency key.
//   If the client retries due to a timeout, the service checks: does a Transfer with this id exist?
//   If yes → return it; if no → execute. Prevents double transfers on network retry.
public record Transfer(
        String     id,
        String     fromAccountId,
        String     toAccountId,
        BigDecimal amount,
        Instant    createdAt
) {}
