package com.interview.ood.q036_bank_service_layer.solution;

// Pattern: Value Object (via enum) — safe to compare with ==, no String equality trap.
// In prod (JPA): @Enumerated(EnumType.STRING) → stored as "CURRENT"/"SAVINGS" in Postgres.
//   Never use EnumType.ORDINAL — adding a new constant in the middle shifts all ordinals → data corruption.
// In prod (Kafka): serialised as a string field in the JSON event payload (AccountOpened).
// Adding ISA/MORTGAGE later = one new constant here + one DB migration — no service logic changes.
public enum AccountType {
    CURRENT,
    SAVINGS
}
