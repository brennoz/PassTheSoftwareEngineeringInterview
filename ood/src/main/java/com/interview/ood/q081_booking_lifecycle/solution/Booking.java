package com.interview.ood.q081_booking_lifecycle.solution;

import java.math.BigDecimal;

// BigDecimal for amount: IEEE 754 double gives 0.8 * 199.99 = 159.99199999999998.
// At millions of bookings these rounding errors accumulate into real money differences.
// BigDecimal stores the exact decimal representation.
//
// NOT a record: JPA requires mutable entities with setters.
// If this were a pure value object (not a DB entity), record would be the right choice.
public class Booking {

    private String id;
    private BookingStatus status;  // typed enum — compiler rejects typos
    private BigDecimal amount;     // exact decimal — never double for money
    private String passengerId;

    public Booking(String id, BookingStatus status, BigDecimal amount, String passengerId) {
        this.id = id;
        this.status = status;
        this.amount = amount;
        this.passengerId = passengerId;
    }

    public String getId()            { return id; }
    public BookingStatus getStatus() { return status; }
    public BigDecimal getAmount()    { return amount; }
    public String getPassengerId()   { return passengerId; }

    // Package-private setter: only BookingService (same package in solution) can change status.
    // Prevents external code bypassing the guard in BookingService — the only place
    // transitions are validated. A public setter would let any caller mutate status directly,
    // defeating the entire state machine.
    void setStatus(BookingStatus status) { this.status = status; }
}
