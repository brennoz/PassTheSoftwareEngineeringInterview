package com.interview.ood.q081_booking_lifecycle.solution;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Design decision: guards live in BookingService, not in Booking.
// Alternative: put transition() method on Booking itself (richer domain model / DDD aggregate).
// Rejected here because Booking is a JPA entity — keeping business logic off JPA entities
// avoids a common Spring anti-pattern where entities become god objects.
// In a proper DDD aggregate, Booking would own its state transitions. For a service-layer
// class in a typical Spring app, the service is the right place.
public class BookingService {

    // Named constant: "80% refund on cancellation" is a named business rule, not a mystery multiplier.
    // Tests assert against this constant — if the rate changes, one place to update.
    private static final BigDecimal CANCELLATION_REFUND_RATE = new BigDecimal("0.80");

    private final BookingRepository repository;

    public BookingService(BookingRepository repository) {
        this.repository = repository;
    }

    // Guard checks valid transition before mutating state.
    // canTransitionFrom() is defined on the enum — adding a new state only requires
    // updating BookingStatus, not touching this method.
    public void confirm(String bookingId) {
        Booking booking = repository.findById(bookingId);
        transition(booking, BookingStatus.CONFIRMED);
        repository.save(booking);
    }

    // TICKETED → CANCELLED throws — the physical ticket is already printed and cannot be cancelled.
    public void cancel(String bookingId) {
        Booking booking = repository.findById(bookingId);
        transition(booking, BookingStatus.CANCELLED);
        repository.save(booking);
    }

    // Throws on invalid state — silent no-ops are worse than exceptions:
    // caller assumes success and proceeds, with no log or signal that nothing happened.
    public void issue(String bookingId) {
        Booking booking = repository.findById(bookingId);
        transition(booking, BookingStatus.TICKETED);
        repository.save(booking);
    }

    // BigDecimal multiply + HALF_UP: standard for financial amounts (£159.992 → £159.99).
    // Return type BigDecimal — callers must handle it correctly (no double coercion).
    public BigDecimal calculateRefund(String bookingId) {
        Booking booking = repository.findById(bookingId);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking.getAmount()
                          .multiply(CANCELLATION_REFUND_RATE)
                          .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    // Single point of transition validation.
    // Why not inline in each method: if we inline, adding a new transition method means
    // remembering to add the guard every time — easy to forget. One private method means
    // there is exactly one place where the guard can be missing, and it isn't missing.
    private void transition(Booking booking, BookingStatus target) {
        BookingStatus current = booking.getStatus();
        if (!target.canTransitionFrom(current)) {
            throw new InvalidStateTransitionException(booking.getId(), current, target);
        }
        booking.setStatus(target);
    }
}
