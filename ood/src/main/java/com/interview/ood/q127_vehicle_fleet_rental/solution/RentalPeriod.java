package com.interview.ood.q127_vehicle_fleet_rental.solution;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Record: immutable value object. Start and end are pinned at construction.
// The compact constructor validates before the record is accessible — impossible
// to construct an invalid period (end before start).
//
// durationDays uses ChronoUnit.DAYS.between rather than end.toEpochDay() - start.toEpochDay()
// for two reasons: (1) ChronoUnit.DAYS is self-documenting — no mental conversion needed;
// (2) ChronoUnit handles calendar-aware edge cases correctly without relying on epoch arithmetic.
//
// Day counting is exclusive of return day:
// A rental from Mon 1st to Tue 2nd = 1 day. You pay for Mon only; you return the car Tuesday.
// This matches how most rental companies bill (calendar-day pricing, not 24-hour blocks).
public record RentalPeriod(LocalDate start, LocalDate end) {

    public RentalPeriod {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                "end must be after start, got start=" + start + " end=" + end);
        }
    }

    public long durationDays() {
        return ChronoUnit.DAYS.between(start, end);
    }
}
