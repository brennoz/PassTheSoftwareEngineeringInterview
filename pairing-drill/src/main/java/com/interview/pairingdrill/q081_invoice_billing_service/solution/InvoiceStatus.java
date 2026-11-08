package com.interview.pairingdrill.q081_invoice_billing_service.solution;

// FIX 2: Replace String magic literals with a typed enum.
// Benefits:
// - Invalid status values caught at compile time
// - All valid transitions discoverable from the enum definition
// - IDE autocomplete — no typos
// - switch expressions exhaustive with Java 21 pattern matching
public enum InvoiceStatus {
    DRAFT,
    ISSUED,
    PARTIALLY_PAID,
    PAID,
    OVERDUE,
    CANCELLED
}
