package com.interview.ood.q036_bank_service_layer.solution;

// Pattern: State (lightweight — two states here; see PaymentStatus in q037 for a full state machine with guards).
// In prod: CLOSED accounts are NEVER deleted — 7-year retention for FCA / SOX audit compliance.
// A more complex lifecycle (PENDING → ACTIVE → FROZEN → CLOSED) would embed canTransitionTo()
// directly in each enum constant so invalid transitions are structurally impossible.
public enum AccountStatus {
    ACTIVE,
    CLOSED
}
