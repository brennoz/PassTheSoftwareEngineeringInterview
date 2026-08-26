package com.interview.ood.q036_bank_service_layer.solution;

import java.util.List;

// Pattern: Repository (Append-Only variant) — Port in Hexagonal Architecture.
// Deliberately has NO update() or delete() method — the interface itself enforces immutability.
// Any future developer adding "deleteTransaction()" to this interface must override the contract,
// making the violation explicit and reviewable. Compare: a mutable repo silently allows deletes.
//
// In prod (Postgres): the DB role used by the application has INSERT-only grants on the
//   ledger table — no UPDATE or DELETE at the SQL level either. Belt and suspenders.
// In prod (S3 / compliance): older entries are archived to S3 with Object Lock (WORM mode)
//   after 90 days — physically immutable, FCA / SOX compliant.
// In prod (Kafka): every record() call also publishes a TransactionRecorded event via Outbox.
//   The Outbox pattern: write to ledger table AND outbox table in one @Transactional,
//   then a CDC process (Debezium) reads the outbox and publishes to Kafka.
//   This guarantees exactly-once: the event is published if and only if the DB commit succeeded.
public interface LedgerRepository {

    // Append only — never called with the same Transaction twice in a correct system.
    // In prod: @Transactional ensures this and the corresponding balance update are atomic.
    void record(Transaction transaction);

    // Returns an unmodifiable view — callers cannot mutate the underlying list.
    // In prod: paginated (findByAccountId(id, PageRequest)) — full history could be millions of rows.
    List<Transaction> history(String accountId);
}
