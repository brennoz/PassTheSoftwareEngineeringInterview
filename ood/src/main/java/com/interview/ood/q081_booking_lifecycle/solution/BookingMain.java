package com.interview.ood.q081_booking_lifecycle.solution;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// Runnable demo — shows the state machine working end-to-end.
// Run: mvn exec:java -pl ood -Dexec.mainClass="com.interview.ood.q081_booking_lifecycle.solution.BookingMain"
public class BookingMain {

    public static void main(String[] args) {
        Map<String, Booking> store = new HashMap<>();
        BookingRepository repo = new InMemoryBookingRepository(store);
        BookingService service = new BookingService(repo);

        // Seed two bookings
        store.put("B001", new Booking("B001", BookingStatus.PENDING,
                new BigDecimal("199.99"), "passenger-1"));
        store.put("B002", new Booking("B002", BookingStatus.PENDING,
                new BigDecimal("89.50"), "passenger-2"));

        // Happy path: PENDING → CONFIRMED → TICKETED
        service.confirm("B001");
        service.issue("B001");
        System.out.println("B001 status: " + store.get("B001").getStatus()); // TICKETED

        // Cancellation with refund: PENDING → CANCELLED
        service.cancel("B002");
        BigDecimal refund = service.calculateRefund("B002");
        System.out.println("B002 refund: £" + refund); // £71.60 (89.50 * 0.80)

        // Invalid: TICKETED → CANCELLED should throw
        try {
            service.cancel("B001");
        } catch (InvalidStateTransitionException e) {
            System.out.println("Caught expected: " + e.getMessage());
        }

        // Invalid: PENDING → TICKETED (skipping CONFIRMED) should throw
        store.put("B003", new Booking("B003", BookingStatus.PENDING,
                new BigDecimal("50.00"), "passenger-3"));
        try {
            service.issue("B003");
        } catch (InvalidStateTransitionException e) {
            System.out.println("Caught expected: " + e.getMessage());
        }
    }

    // Simple in-memory repository for the demo — not part of the OOD solution itself
    private static class InMemoryBookingRepository implements BookingRepository {
        private final Map<String, Booking> store;

        InMemoryBookingRepository(Map<String, Booking> store) {
            this.store = store;
        }

        @Override
        public Booking findById(String id) {
            Booking b = store.get(id);
            if (b == null) throw new IllegalArgumentException("Booking not found: " + id);
            return b;
        }

        @Override
        public void save(Booking booking) {
            store.put(booking.getId(), booking);
        }
    }
}
