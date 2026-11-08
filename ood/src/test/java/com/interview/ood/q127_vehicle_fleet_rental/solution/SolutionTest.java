package com.interview.ood.q127_vehicle_fleet_rental.solution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Tests are grouped by concern — state machine, observer, pricing — so a failure
// in one area doesn't mask a failure in another. Each group is independent: a pricing
// bug will not cause a state-machine test to fail.
//
// No AssertJ per project convention — plain JUnit 5 assertions throughout.
// BigDecimal comparisons use assertEquals(0, actual.compareTo(expected)) to avoid
// scale sensitivity: BigDecimal.equals() considers 150.00 != 150.0.
class SolutionTest {

    private static final LocalDate DATE = LocalDate.of(2026, 1, 1);

    // --- State machine ---

    @Test
    void vehicle_completes_full_rental_lifecycle() {
        // Why one test for the full lifecycle rather than one assertion per transition?
        // Each transition depends on the prior state. Splitting into five tests would
        // either duplicate setup (verbose, fragile) or rely on test order (wrong).
        // A single narrative test is the right shape for a sequential state machine.
        Vehicle vehicle = new Vehicle("V-001", VehicleClass.STANDARD,
                new BigDecimal("45000"), new BigDecimal("200000"));

        vehicle.reserve("CUST-1");
        assertEquals(VehicleStatus.RESERVED, vehicle.getStatus());

        vehicle.checkout(new BigDecimal("45000"));
        assertEquals(VehicleStatus.IN_USE, vehicle.getStatus());

        vehicle.returnVehicle(new BigDecimal("46000"));
        assertEquals(VehicleStatus.AWAITING_INSPECTION, vehicle.getStatus());

        vehicle.inspectionPassed();
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }

    @Test
    void illegal_transition_throws_and_leaves_status_unchanged() {
        // Verifies both the exception and that the guard aborts before mutating state.
        // Testing only the exception would leave open the bug where status is changed
        // before the guard check fires.
        Vehicle vehicle = new Vehicle("V-002", VehicleClass.ECONOMY,
                new BigDecimal("10000"), new BigDecimal("100000"));
        // AVAILABLE → AWAITING_INSPECTION is not a valid transition.
        assertThrows(InvalidStateTransitionException.class,
                () -> vehicle.returnVehicle(new BigDecimal("10500")));
        // Status must not have changed — the exception must fire before any assignment.
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }

    // --- Observer ---

    @Test
    void observer_notified_with_correct_args_when_threshold_crossed() {
        // Why Mockito's verify() over a hand-rolled flag?
        // A boolean[] flag only confirms the observer fired — not what arguments it received.
        // verify(observer, times(1)).onMaintenanceRequired("V-003", new BigDecimal("50200"))
        // asserts both "called exactly once" AND "with these exact arguments."
        // A wrong vehicleId or wrong mileage would be caught here; a flag would miss it.
        MaintenanceObserver observer = mock(MaintenanceObserver.class);
        Vehicle vehicle = new Vehicle("V-003", VehicleClass.PREMIUM,
                new BigDecimal("49000"), new BigDecimal("50000"));
        vehicle.addObserver(observer);

        vehicle.reserve("CUST-3");
        vehicle.checkout(new BigDecimal("49000"));
        vehicle.returnVehicle(new BigDecimal("50200")); // 49,000 < threshold, 50,200 >= threshold

        verify(observer, times(1)).onMaintenanceRequired("V-003", new BigDecimal("50200"));
    }

    @Test
    void observer_not_notified_when_mileage_stays_below_threshold() {
        // Negative test: confirms the observer does NOT fire when the threshold is not crossed.
        // Without this test, an always-firing implementation would pass the positive test above.
        MaintenanceObserver observer = mock(MaintenanceObserver.class);
        Vehicle vehicle = new Vehicle("V-004", VehicleClass.ECONOMY,
                new BigDecimal("40000"), new BigDecimal("50000"));
        vehicle.addObserver(observer);

        vehicle.reserve("CUST-4");
        vehicle.checkout(new BigDecimal("40000"));
        vehicle.returnVehicle(new BigDecimal("41000")); // 41,000 < 50,000 threshold

        verify(observer, never()).onMaintenanceRequired(any(), any());
    }

    // --- Pricing strategy ---

    @Test
    void pricing_no_discount_for_rentals_under_7_days() {
        Vehicle vehicle = new Vehicle("V-005", VehicleClass.STANDARD,
                BigDecimal.ZERO, new BigDecimal("200000"));
        // 3 days × £50 × 1.00 = £150.00
        Money cost = vehicle.getRentalCost(new RentalPeriod(DATE, DATE.plusDays(3)));
        assertEquals(0, cost.amount().compareTo(new BigDecimal("150.00")));
        assertEquals("GBP", cost.currency());
    }

    @Test
    void pricing_weekly_discount_applied_at_exactly_7_days() {
        // Tests the ">= 7" boundary — "exactly 7 days" must qualify for the 15% discount.
        // A common off-by-one: "> 7" would miss this case and charge full rate.
        Vehicle vehicle = new Vehicle("V-006", VehicleClass.LUXURY,
                BigDecimal.ZERO, new BigDecimal("200000"));
        // 7 days × £130 × 0.85 = £773.50
        Money cost = vehicle.getRentalCost(new RentalPeriod(DATE, DATE.plusDays(7)));
        assertEquals(0, cost.amount().compareTo(new BigDecimal("773.50")));
    }

    @Test
    void pricing_monthly_discount_applied_at_28_days() {
        Vehicle vehicle = new Vehicle("V-007", VehicleClass.ECONOMY,
                BigDecimal.ZERO, new BigDecimal("200000"));
        // 28 days × £30 × 0.75 = £630.00
        Money cost = vehicle.getRentalCost(new RentalPeriod(DATE, DATE.plusDays(28)));
        assertEquals(0, cost.amount().compareTo(new BigDecimal("630.00")));
    }
}
