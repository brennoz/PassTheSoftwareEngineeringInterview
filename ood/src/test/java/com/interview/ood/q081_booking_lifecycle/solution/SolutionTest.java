package com.interview.ood.q081_booking_lifecycle.solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolutionTest {

    private BookingRepository repo;
    private BookingService service;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(BookingRepository.class);
        service = new BookingService(repo);
    }

    // --- Happy path ---

    @Test
    void confirm_pendingBooking_transitionsToConfirmed() {
        Booking b = booking("B1", BookingStatus.PENDING, "100.00");
        when(repo.findById("B1")).thenReturn(b);

        service.confirm("B1");

        assertEquals(BookingStatus.CONFIRMED, b.getStatus());
        verify(repo).save(b);
    }

    @Test
    void issue_confirmedBooking_transitionsToTicketed() {
        Booking b = booking("B2", BookingStatus.CONFIRMED, "100.00");
        when(repo.findById("B2")).thenReturn(b);

        service.issue("B2");

        assertEquals(BookingStatus.TICKETED, b.getStatus());
    }

    @Test
    void cancel_pendingBooking_transitionsToCancelled() {
        Booking b = booking("B3", BookingStatus.PENDING, "100.00");
        when(repo.findById("B3")).thenReturn(b);

        service.cancel("B3");

        assertEquals(BookingStatus.CANCELLED, b.getStatus());
    }

    // --- Refund calculation ---

    @Test
    void calculateRefund_cancelledBooking_returnsEightyPercent() {
        // 199.99 * 0.80 = 159.992 → rounds to 159.99 (HALF_UP, 2dp)
        // This is the test that would FAIL with double: double gives 159.99199999999998
        Booking b = booking("B4", BookingStatus.CANCELLED, "199.99");
        when(repo.findById("B4")).thenReturn(b);

        BigDecimal refund = service.calculateRefund("B4");

        // compareTo instead of equals — BigDecimal.equals is scale-sensitive (159.99 != 159.990)
        assertEquals(0, refund.compareTo(new BigDecimal("159.99")));
    }

    @Test
    void calculateRefund_nonCancelledBooking_returnsZero() {
        Booking b = booking("B5", BookingStatus.CONFIRMED, "100.00");
        when(repo.findById("B5")).thenReturn(b);

        assertEquals(0, service.calculateRefund("B5").compareTo(BigDecimal.ZERO));
    }

    // --- Guard: invalid transitions must throw ---

    @Test
    void cancel_ticketedBooking_throwsInvalidStateTransition() {
        // A printed ticket cannot be cancelled — this is the production exploit that was
        // silently allowed in the seeded code (TICKETED → CANCELLED had no guard).
        Booking b = booking("B6", BookingStatus.TICKETED, "100.00");
        when(repo.findById("B6")).thenReturn(b);

        assertThrows(InvalidStateTransitionException.class, () -> service.cancel("B6"));
        // State must NOT have changed — save() must not be called after the exception
        verify(repo, never()).save(any());
    }

    @Test
    void issue_cancelledBooking_throwsInvalidStateTransition() {
        // The seeded code silently did nothing — now it throws.
        // Silent no-ops are worse than exceptions: callers assume success and proceed.
        Booking b = booking("B7", BookingStatus.CANCELLED, "100.00");
        when(repo.findById("B7")).thenReturn(b);

        assertThrows(InvalidStateTransitionException.class, () -> service.issue("B7"));
    }

    @Test
    void confirm_cancelledBooking_throwsInvalidStateTransition() {
        // The exploit that allowed re-confirming after cancellation.
        Booking b = booking("B8", BookingStatus.CANCELLED, "100.00");
        when(repo.findById("B8")).thenReturn(b);

        assertThrows(InvalidStateTransitionException.class, () -> service.confirm("B8"));
    }

    // Helper — keeps test bodies clean
    private Booking booking(String id, BookingStatus status, String amount) {
        return new Booking(id, status, new BigDecimal(amount), "passenger-x");
    }
}
