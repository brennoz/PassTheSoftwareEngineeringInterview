package com.interview.ood.q037_payment_gateway.solution;

// Pattern: State (as an enum) — each state's allowed next-states live ON the constant itself,
// not in a switch statement in PaymentService. Adding a new status only means adding a
// constant + its canTransitionTo override, never touching every place that currently switches
// on PaymentStatus. What NOT to do: put the transition table in PaymentService as
// `if (status == INITIATED && next == AUTHORISED)` chains — that scatters the state machine
// across the service and grows unreadable as statuses/transitions increase.
public enum PaymentStatus {
    INITIATED {
        @Override
        public boolean canTransitionTo(PaymentStatus next) {
            return next == AUTHORISED || next == FAILED;
        }
    },
    AUTHORISED {
        @Override
        public boolean canTransitionTo(PaymentStatus next) {
            return next == CAPTURED || next == VOIDED;
        }
    },
    CAPTURED {
        @Override
        public boolean canTransitionTo(PaymentStatus next) {
            return next == SETTLED || next == REFUNDED;
        }
    },
    SETTLED,
    VOIDED,
    REFUNDED,
    FAILED;

    // Terminal states (SETTLED, VOIDED, REFUNDED, FAILED) don't override this — they fall
    // through to `false` for every target, which is exactly right: nothing transitions out of
    // a terminal state.
    public boolean canTransitionTo(PaymentStatus next) {
        return false;
    }

    public void assertCanTransitionTo(PaymentStatus next) {
        if (!canTransitionTo(next)) {
            throw new IllegalStateException("Cannot transition from " + this + " to " + next);
        }
    }
}
