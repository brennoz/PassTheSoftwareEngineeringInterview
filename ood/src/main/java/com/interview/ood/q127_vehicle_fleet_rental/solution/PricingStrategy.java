package com.interview.ood.q127_vehicle_fleet_rental.solution;

// Strategy interface: Vehicle delegates cost calculation without knowing which tier
// rules, base rates, or discount multipliers apply. Vehicle stays stable when:
//   - A new VehicleClass is added (new rate, same interface)
//   - A new pricing model is introduced (MLPricingStrategy, CorporatePricingStrategy)
//   - A peak-season surcharge is layered on (Decorator over this interface)
//
// Alternative rejected: switch(vehicleClass) inside Vehicle.getRentalCost().
// Rejected because adding a new pricing model (subscription, corporate) requires
// editing Vehicle — a class whose primary responsibility is lifecycle management,
// not pricing arithmetic.
public interface PricingStrategy {
    Money calculateCost(RentalPeriod period);
}
