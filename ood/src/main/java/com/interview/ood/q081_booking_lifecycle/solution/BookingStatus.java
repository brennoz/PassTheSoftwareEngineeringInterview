package com.interview.ood.q081_booking_lifecycle.solution;

import java.util.Set;

// Fix for Issue 1: String status → typed enum.
// Why enum over String: the compiler rejects BookingStatus.CONFIMED at compile time.
// A String "CONFIMED" silently compiles and fails at runtime — exactly what caused the
// CANCELLED→CONFIRMED exploit (a mis-typed equality check let an invalid transition through).
//
// Why encode valid predecessors here rather than in BookingService:
// BookingService would need to know state machine rules for every state it touches.
// Putting the rules on the enum means adding a new state (e.g. ON_HOLD) only requires
// adding one enum constant — BookingService itself never changes.
// This is the Open/Closed principle: open for new states, closed for modification.
//
// Alternative rejected: a separate Map<BookingStatus, Set<BookingStatus>> in BookingService.
// Rejected because it splits the state definition (enum) from its transition rules (map),
// making it easy to add a new enum value and forget to update the map.
public enum BookingStatus {

    PENDING(Set.of()),
    CONFIRMED(Set.of(PENDING)),
    TICKETED(Set.of(CONFIRMED)),
    CANCELLED(Set.of(PENDING, CONFIRMED));
    // TICKETED is deliberately absent from CANCELLED's valid predecessors —
    // a printed ticket cannot be cancelled. The enum makes this rule explicit and testable.

    private final Set<BookingStatus> validPredecessors;

    BookingStatus(Set<BookingStatus> validPredecessors) {
        this.validPredecessors = validPredecessors;
    }

    // Whether transitioning FROM the given current status TO this status is legal.
    // Called by BookingService before every state change.
    public boolean canTransitionFrom(BookingStatus current) {
        return validPredecessors.contains(current);
    }
}
