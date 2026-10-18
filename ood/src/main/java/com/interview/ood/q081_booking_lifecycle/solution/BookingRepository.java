package com.interview.ood.q081_booking_lifecycle.solution;

public interface BookingRepository {
    Booking findById(String bookingId);
    void save(Booking booking);
}
