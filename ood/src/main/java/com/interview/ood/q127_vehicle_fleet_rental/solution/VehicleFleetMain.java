package com.interview.ood.q127_vehicle_fleet_rental.solution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Runnable demonstration of the full solution end-to-end.
// Four scenarios: full lifecycle, pricing tiers, invalid transition guard,
// and the inspection-failed → maintenance → repair path.
// Not a test — just observable output to confirm the patterns wire correctly.
public class VehicleFleetMain {

    public static void main(String[] args) {
        System.out.println("=== Vehicle Fleet Rental — Solution Demo ===\n");

        // --- Scenario 1: full lifecycle with maintenance observer ---
        System.out.println("--- 1. Full lifecycle + maintenance observer ---");
        Vehicle standard = new Vehicle("V-001", VehicleClass.STANDARD,
                new BigDecimal("49000"), new BigDecimal("50000"));

        List<String> alerts = new ArrayList<>();
        standard.addObserver((vehicleId, mileage) ->
                alerts.add("[ALERT] Vehicle " + vehicleId + " reached " + mileage + " km — schedule service"));

        System.out.println("Initial status : " + standard.getStatus());            // AVAILABLE

        standard.reserve("CUST-42");
        System.out.println("After reserve  : " + standard.getStatus());            // RESERVED

        standard.checkout(new BigDecimal("49000"));
        System.out.println("After checkout : " + standard.getStatus());            // IN_USE

        // Return with odometer crossing the 50,000 km threshold
        standard.returnVehicle(new BigDecimal("50200"));
        System.out.println("After return   : " + standard.getStatus());            // AWAITING_INSPECTION
        System.out.println("Mileage        : " + standard.getCurrentMileage());    // 50200
        System.out.println("Alerts fired   : " + alerts);                          // one alert

        standard.inspectionPassed();
        System.out.println("After inspection passed: " + standard.getStatus());    // AVAILABLE

        // --- Scenario 2: pricing tiers (PREMIUM, £80/day) ---
        System.out.println("\n--- 2. Pricing tiers (PREMIUM, £80/day) ---");
        Vehicle premium = new Vehicle("V-002", VehicleClass.PREMIUM,
                new BigDecimal("10000"), new BigDecimal("200000"));

        LocalDate base = LocalDate.of(2026, 1, 1);

        Money cost3  = premium.getRentalCost(new RentalPeriod(base, base.plusDays(3)));
        Money cost7  = premium.getRentalCost(new RentalPeriod(base, base.plusDays(7)));
        Money cost28 = premium.getRentalCost(new RentalPeriod(base, base.plusDays(28)));

        System.out.printf(" 3 days  (no discount)      : %s %s%n",
                cost3.currency(), cost3.amount());     // GBP 240.00   (3 × 80 × 1.00)
        System.out.printf(" 7 days  (15%% weekly off)   : %s %s%n",
                cost7.currency(), cost7.amount());     // GBP 476.00   (7 × 80 × 0.85)
        System.out.printf("28 days  (25%% monthly off)  : %s %s%n",
                cost28.currency(), cost28.amount());   // GBP 1680.00  (28 × 80 × 0.75)

        // --- Scenario 3: invalid transition guard ---
        System.out.println("\n--- 3. Invalid transition guard ---");
        Vehicle luxury = new Vehicle("V-003", VehicleClass.LUXURY,
                new BigDecimal("5000"), new BigDecimal("100000"));
        try {
            // AVAILABLE → AWAITING_INSPECTION: illegal — must go AVAILABLE → RESERVED → IN_USE first
            luxury.returnVehicle(new BigDecimal("5500"));
        } catch (InvalidStateTransitionException e) {
            System.out.println("Caught expected: " + e.getMessage());
        }
        System.out.println("Status unchanged: " + luxury.getStatus());             // AVAILABLE

        // --- Scenario 4: inspection failed → maintenance → repair ---
        System.out.println("\n--- 4. Inspection failed → maintenance → repair ---");
        Vehicle economy = new Vehicle("V-004", VehicleClass.ECONOMY,
                new BigDecimal("90000"), new BigDecimal("100000"));

        economy.reserve("CUST-99");
        economy.checkout(new BigDecimal("90000"));
        economy.returnVehicle(new BigDecimal("91000"));
        System.out.println("After return             : " + economy.getStatus());   // AWAITING_INSPECTION
        economy.inspectionFailed();
        System.out.println("After inspection failed  : " + economy.getStatus());   // MAINTENANCE
        economy.repairComplete();
        System.out.println("After repair complete    : " + economy.getStatus());   // AVAILABLE

        // --- Scenario 5: observer NOT fired when already past threshold at checkout ---
        System.out.println("\n--- 5. No duplicate alert when already past threshold ---");
        Vehicle highMileage = new Vehicle("V-005", VehicleClass.ECONOMY,
                new BigDecimal("60000"), new BigDecimal("50000")); // already past 50k
        List<String> duplicateAlerts = new ArrayList<>();
        highMileage.addObserver((vehicleId, mileage) -> duplicateAlerts.add("ALERT for " + vehicleId));

        highMileage.reserve("CUST-77");
        highMileage.checkout(new BigDecimal("60000"));   // already past threshold
        highMileage.returnVehicle(new BigDecimal("61000")); // still past — no new crossing
        System.out.println("Duplicate alerts (expect 0): " + duplicateAlerts.size());
    }
}
