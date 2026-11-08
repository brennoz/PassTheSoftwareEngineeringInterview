package com.interview.ood.q127_vehicle_fleet_rental.solution;

// Unchecked (RuntimeException) — callers are expected to call canTransitionTo() first.
// A checked exception would force every caller to catch it even when a guard check
// is already in place upstream, adding noise without safety benefit.
//
// Carries enough context to debug without a stack trace: vehicle ID, from-state, to-state.
// NOT TO DO: throwing a generic IllegalStateException("invalid state") — that message
// tells you nothing about which vehicle, which transition, or what was expected.
public class InvalidStateTransitionException extends RuntimeException {

    public InvalidStateTransitionException(String vehicleId, VehicleStatus from, VehicleStatus to) {
        super("Vehicle " + vehicleId + ": illegal transition " + from + " → " + to);
    }
}
