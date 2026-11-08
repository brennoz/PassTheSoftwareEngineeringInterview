package com.interview.ood.q127_vehicle_fleet_rental.solution;

// Pattern: enum state machine with canTransitionTo() guard.
//
// Why encode transition rules ON the enum constant rather than in a Map in Vehicle?
// A Map<VehicleStatus, Set<VehicleStatus>> in Vehicle splits the state definition
// (the enum) from its transition rules (the map). Adding UNDER_RECALL requires
// editing both the enum AND the map — easy to update one and forget the other,
// and the compiler won't catch the omission.
// With rules on the enum: adding UNDER_RECALL = one new constant here, which is
// also the only place its allowed successors are declared. Vehicle.transitionTo()
// never changes. This is the Open/Closed principle applied to a state machine.
//
// canTransitionTo vs canTransitionFrom (used in q081):
// Both are equivalent. canTransitionTo reads naturally at the Vehicle call site:
// "status.canTransitionTo(next)" — "can my current state transition to next?"
// canTransitionFrom reads from the target's perspective. Pick one and be consistent.
public enum VehicleStatus {

    AVAILABLE {
        @Override
        public boolean canTransitionTo(VehicleStatus next) {
            // AVAILABLE → IN_USE direct path is intentionally blocked.
            // The RESERVED step is mandatory so there is always a record linking
            // a customer to a vehicle before keys change hands. A vehicle
            // going directly to IN_USE has no reservation record — unaccountable.
            return next == RESERVED || next == RETIRED;
        }
    },
    RESERVED {
        @Override
        public boolean canTransitionTo(VehicleStatus next) {
            // Reservation can be cancelled (back to AVAILABLE) or fulfilled (checkout → IN_USE).
            // RESERVED → RETIRED is blocked: a reserved vehicle is committed to a customer;
            // retire it only after the reservation is cancelled first.
            return next == AVAILABLE || next == IN_USE;
        }
    },
    IN_USE {
        @Override
        public boolean canTransitionTo(VehicleStatus next) {
            // Every return triggers an inspection — no shortcut directly to AVAILABLE.
            // NOT TO DO: allowing IN_USE → AVAILABLE would let a vehicle with hidden
            // damage (cracked windscreen, engine fault) go straight back onto the fleet.
            return next == AWAITING_INSPECTION;
        }
    },
    AWAITING_INSPECTION {
        @Override
        public boolean canTransitionTo(VehicleStatus next) {
            // Passed → AVAILABLE (ready to rent again); failed → MAINTENANCE (needs repair).
            return next == AVAILABLE || next == MAINTENANCE;
        }
    },
    MAINTENANCE {
        @Override
        public boolean canTransitionTo(VehicleStatus next) {
            // A repaired vehicle returns to service, or is retired if repair is uneconomical.
            return next == AVAILABLE || next == RETIRED;
        }
    },
    RETIRED {
        @Override
        public boolean canTransitionTo(VehicleStatus next) {
            // Terminal state. Once a vehicle is de-registered from the fleet it cannot
            // return — real-world de-registration (DVLA, insurance) is irreversible.
            return false;
        }
    };

    public abstract boolean canTransitionTo(VehicleStatus next);
}
