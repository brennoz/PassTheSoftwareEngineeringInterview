package com.interview.pairingdrill.q079_account_statement.solution;

// Fix 4: enum replaces String type field on Transaction.
// Why: "CREDIT" vs "credit" vs "Credit" are different strings — any casing inconsistency
// at a call site produces a silent filter miss (totals silently return zero, no exception).
// With an enum, a wrong value is a compile error, not a runtime surprise.
// Alternative rejected: String constants (e.g. static final String CREDIT = "CREDIT") —
// callers can still pass arbitrary strings, so only correct-callers are protected, not
// wrong values injected from deserialization or external input parsing.
public enum TransactionType {
    CREDIT,
    DEBIT
}
