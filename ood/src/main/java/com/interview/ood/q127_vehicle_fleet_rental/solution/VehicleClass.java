package com.interview.ood.q127_vehicle_fleet_rental.solution;

import java.math.BigDecimal;

// Each enum constant carries its own daily rate — Open/Closed for new classes.
// Adding ELECTRIC only requires a new constant here; PricingStrategyFactory.forClass()
// reads vehicleClass.getDailyRateGbp() directly, so it picks up the new rate automatically.
// No switch statement in the factory — no place to forget to update.
//
// Alternative rejected: store rates in a Map<VehicleClass, BigDecimal> in PricingStrategyFactory.
// Rejected because the rate is a property of the class (not of the factory); keeping it
// here avoids a two-file edit when adding or changing a class.
//
// new BigDecimal("30.00") NOT new BigDecimal(30.00):
// The double literal 30.00 is already imprecise before BigDecimal sees it —
// new BigDecimal(30.00) can produce 30.000000000000003552... in some JVMs.
// String constructor guarantees exact value.
public enum VehicleClass {

    ECONOMY (new BigDecimal("30.00")),
    STANDARD(new BigDecimal("50.00")),
    PREMIUM (new BigDecimal("80.00")),
    LUXURY  (new BigDecimal("130.00"));

    private final BigDecimal dailyRateGbp;

    VehicleClass(BigDecimal dailyRateGbp) {
        this.dailyRateGbp = dailyRateGbp;
    }

    public BigDecimal getDailyRateGbp() {
        return dailyRateGbp;
    }
}
