package com.interview.ood.q081_booking_lifecycle.solution;

// Unchecked exception: callers are not forced to catch it — invalid transitions
// are programming errors (or data integrity violations), not recoverable conditions
// a normal call site can handle.  Using RuntimeException avoids polluting every
// call site with try/catch for a condition that should never occur in correct code.
public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String bookingId,
                                           BookingStatus current,
                                           BookingStatus target) {
        super("Cannot transition booking " + bookingId
              + " from " + current + " to " + target);
    }
}
