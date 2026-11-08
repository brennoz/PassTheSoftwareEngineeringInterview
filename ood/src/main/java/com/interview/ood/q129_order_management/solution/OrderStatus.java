package com.interview.ood.q129_order_management.solution;

// Pattern: State machine via enum with abstract canTransitionTo() per constant.
// Same approach as q127 VehicleStatus and q128 InvoiceStatus — guards live on the source
// state, so adding a new status never forces editing every existing constant.
// What NOT to do: put all transition logic in Order.java as a big switch — OCP violation,
// and adding a state requires finding and editing that switch rather than touching one constant.
public enum OrderStatus {

    NEW {
        @Override public boolean canTransitionTo(OrderStatus next) {
            return next == PENDING_ACK;
        }
    },
    PENDING_ACK {
        @Override public boolean canTransitionTo(OrderStatus next) {
            return next == ACKNOWLEDGED || next == REJECTED;
        }
    },
    ACKNOWLEDGED {
        @Override public boolean canTransitionTo(OrderStatus next) {
            return next == PARTIALLY_FILLED || next == FILLED || next == CANCELLED || next == EXPIRED;
        }
    },
    PARTIALLY_FILLED {
        // Alternative considered: merge PARTIALLY_FILLED into ACKNOWLEDGED with a filledQty field.
        // Rejected: a separate status makes the lifecycle explicit in logs, dashboards, and MiFID II
        // audit trails — a reader sees "partially filled then cancelled" without inspecting quantities.
        @Override public boolean canTransitionTo(OrderStatus next) {
            return next == PARTIALLY_FILLED || next == FILLED || next == CANCELLED;
        }
    },
    FILLED {
        @Override public boolean canTransitionTo(OrderStatus next) { return false; }
    },
    CANCELLED {
        @Override public boolean canTransitionTo(OrderStatus next) { return false; }
    },
    REJECTED {
        // REJECTED is terminal — re-submission must create a new order with a new ID.
        // What NOT to do: allow REJECTED → NEW, which would mutate the audit record rather than
        // creating a new one — incompatible with event-sourcing replay and MiFID II audit requirements.
        @Override public boolean canTransitionTo(OrderStatus next) { return false; }
    },
    EXPIRED {
        @Override public boolean canTransitionTo(OrderStatus next) { return false; }
    };

    public abstract boolean canTransitionTo(OrderStatus next);
}
