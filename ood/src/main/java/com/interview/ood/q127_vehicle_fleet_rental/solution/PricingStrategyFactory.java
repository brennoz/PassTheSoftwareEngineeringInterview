package com.interview.ood.q127_vehicle_fleet_rental.solution;

// Factory: Vehicle asks "give me a pricing strategy for my class" without building
// one itself. This decouples Vehicle from VehiclePricingStrategy's constructor.
//
// Why not pass the strategy into Vehicle's constructor?
// Dependency injection (constructor DI) also works and is arguably cleaner for testing.
// The factory approach is used here to show the pattern explicitly, and to enforce
// "one canonical strategy per class" in a single place — callers cannot accidentally
// wire an ECONOMY rate to a LUXURY vehicle.
//
// Adding a new VehicleClass requires zero changes here: forClass() reads
// vehicleClass.getDailyRateGbp() — the rate travels with the enum constant.
// This is why the rate lives on VehicleClass and not in a Map here.
public final class PricingStrategyFactory {

    private static final String DEFAULT_CURRENCY = "GBP";

    public static PricingStrategy forClass(VehicleClass vehicleClass) {
        return new VehiclePricingStrategy(vehicleClass.getDailyRateGbp(), DEFAULT_CURRENCY);
    }

    // Utility class — no instances.
    private PricingStrategyFactory() {}
}
