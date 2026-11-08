package com.interview.spring.q128_invoice_payment_service.solution;

// Enum state machine — same design as VehicleStatus in q127 (canTransitionTo on source state).
// Terminal states (PAID, CANCELLED) override with return false — no separate abstract method
// needed since Java allows non-abstract enums with abstract methods only on bodies.
//
// Why not a Map<InvoiceStatus, Set<InvoiceStatus>> in InvoiceService?
// Same reason as q127: splits state definition from transition rules — adding DISPUTED
// requires editing both the enum AND the map. Rules here = one edit, one place.
//
// OVERDUE can receive partial payments and full payments — overdue doesn't prevent payment,
// it just means the due date passed. OVERDUE → PARTIALLY_PAID handles the case where
// a customer catches up partially after the due date.
public enum InvoiceStatus {

    DRAFT {
        @Override public boolean canTransitionTo(InvoiceStatus next) {
            return next == ISSUED || next == CANCELLED;
        }
    },
    ISSUED {
        @Override public boolean canTransitionTo(InvoiceStatus next) {
            return next == PARTIALLY_PAID || next == PAID || next == OVERDUE || next == CANCELLED;
        }
    },
    PARTIALLY_PAID {
        @Override public boolean canTransitionTo(InvoiceStatus next) {
            return next == PAID || next == OVERDUE || next == CANCELLED;
        }
    },
    OVERDUE {
        @Override public boolean canTransitionTo(InvoiceStatus next) {
            // OVERDUE can receive payments — doesn't prevent payment, just flags late.
            return next == PAID || next == PARTIALLY_PAID || next == CANCELLED;
        }
    },
    PAID {
        @Override public boolean canTransitionTo(InvoiceStatus next) { return false; }
    },
    CANCELLED {
        @Override public boolean canTransitionTo(InvoiceStatus next) { return false; }
    };

    public abstract boolean canTransitionTo(InvoiceStatus next);
}
