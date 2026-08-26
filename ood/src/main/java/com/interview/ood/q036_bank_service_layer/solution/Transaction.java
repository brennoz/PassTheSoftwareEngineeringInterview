package com.interview.ood.q036_bank_service_layer.solution;

import java.math.BigDecimal;
import java.time.Instant;

// Pattern: Immutable Ledger Entry (Append-Only Log).
// Transactions are NEVER updated or deleted — fundamental accounting invariant.
// The ledger is a chronological log; current balance = fold(transactions, BigDecimal::add).
// This is the foundation of Event Sourcing: state = reduce(events).
//
// Why BigDecimal and not double?
//   double: 0.1 + 0.2 = 0.30000000000000004 (IEEE 754 cannot represent 1/10 exactly).
//   At scale (millions of transactions), rounding error accumulates into real missing money.
//   FCA / PRA rules mandate exact decimal arithmetic. Use HALF_EVEN rounding (banker's rounding —
//   statistically neutral across large volumes, unlike HALF_UP which introduces systematic bias).
//
// balanceAfter: stored on every entry for O(1) point-in-time balance queries.
//   Without it: "balance on 2024-01-15" requires summing ALL prior transactions — O(N) scan.
//   With it: single indexed query on (accountId, createdAt) → read balanceAfter of last entry.
//
// Instant (UTC): always store in UTC. Local timezone timestamps break during DST transitions —
//   you get duplicate or missing hours in logs, corrupting time-series aggregations.
//
// In prod (Kafka): published as TransactionRecorded event on topic "bank.transactions",
//   partitioned by accountId — all events for one account are ordered and land on the same
//   consumer instance (preserves causal ordering without distributed coordination).
// In prod (Postgres): append-only table, no UPDATE/DELETE grants on this table in the DB role.
//   Separate read-only reporting role queries it. Indexes: (accountId, createdAt DESC).
public record Transaction(
        String          id,
        String          accountId,
        TransactionType type,
        BigDecimal      amount,
        BigDecimal      balanceAfter,
        Instant         createdAt
) {}
