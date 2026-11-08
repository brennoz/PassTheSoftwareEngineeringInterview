package com.interview.ood.q127_vehicle_fleet_rental.solution;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Single implementation parameterised by daily rate — avoids four nearly-identical
// EconomyPricingStrategy, StandardPricingStrategy, ... classes that share identical
// tier logic and differ only in a constructor constant.
//
// When WOULD four separate classes make sense? Only if a class needs qualitatively
// different tier rules (e.g. LUXURY has a 3-day minimum — charging fewer than 3 days
// still bills as 3). That cannot be expressed by a multiplier; it needs its own logic.
// Until then, one class + a constructor parameter is the right amount of abstraction.
//
// Duration tiers — why these boundaries?
// 7 days = one week: natural booking horizon, discount incentivises commitment.
// 28 days = four weeks: monthly commitment, deepest discount.
// Both are business rules, not algorithm constraints — they live as named constants,
// not magic numbers embedded in an if chain.
//
// NOT TO DO: new BigDecimal(0.85) — the double literal 0.85 cannot be represented
// exactly in IEEE 754 binary, so new BigDecimal(0.85) = 0.84999999999999997779...
// String constructor new BigDecimal("0.85") gives the exact intended value.
public class VehiclePricingStrategy implements PricingStrategy {

    private static final BigDecimal WEEKLY_MULTIPLIER  = new BigDecimal("0.85");
    private static final BigDecimal MONTHLY_MULTIPLIER = new BigDecimal("0.75");

    private final BigDecimal dailyRate;
    private final String currency;

    public VehiclePricingStrategy(BigDecimal dailyRate, String currency) {
        this.dailyRate = dailyRate;
        this.currency  = currency;
    }

    @Override
    public Money calculateCost(RentalPeriod period) {
        long days = period.durationDays();
        BigDecimal multiplier = discountMultiplierFor(days);
        // Multiplication order: rate × days first (integer result), then × multiplier.
        // Equivalent mathematically, but integer × integer avoids fractional intermediate
        // values before the multiplier is applied — rounding is applied once at the end.
        BigDecimal total = dailyRate
            .multiply(BigDecimal.valueOf(days))
            .multiply(multiplier)
            .setScale(2, RoundingMode.HALF_UP);
        return new Money(total, currency);
    }

    // NOT TO DO: if (days > 7) — off-by-one: a 7-day rental misses the weekly discount.
    // The boundary is >= 7 (inclusive). Common interview trap in pricing code.
    private BigDecimal discountMultiplierFor(long days) {
        if (days >= 28) return MONTHLY_MULTIPLIER;
        if (days >= 7)  return WEEKLY_MULTIPLIER;
        return BigDecimal.ONE;
    }
}
