package com.interview.ood.q129_order_management.solution;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Value object: immutable, equality by value not identity.
// Why BigDecimal, not double: double cannot represent 0.1 exactly. At scale, floating-point
// drift produces wrong fills — wrong by a fraction of a cent is still regulatory non-compliance
// under MiFID II best-execution rules. This is the same rule as q128's Money and q077's stakes.
public record Price(BigDecimal value, String currency) {

    // Compact constructor: validate and normalise to 6 decimal places.
    // 6dp covers equities (4dp typical) and FX (5dp typical) with headroom.
    // What NOT to do: skip the scale-normalisation — two Price instances representing the
    // same economic value but different scales will fail .equals() on their BigDecimal fields,
    // causing missed dedup checks in execution matching.
    public Price {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be non-negative: " + value);
        if (currency == null || currency.isBlank())
            throw new IllegalArgumentException("Currency required");
        value = value.setScale(6, RoundingMode.HALF_UP);
    }

    public static Price of(String currency, String value) {
        return new Price(new BigDecimal(value), currency);
    }

    public static Price of(String currency, BigDecimal value) {
        return new Price(value, currency);
    }

    // compareTo for scale-independent comparison — BigDecimal.equals() is scale-sensitive:
    // new BigDecimal("185.50").equals(new BigDecimal("185.5")) is false.
    public boolean isGreaterThan(Price other) {
        sameCurrencyGuard(other);
        return this.value.compareTo(other.value) > 0;
    }

    public boolean isLessThan(Price other) {
        sameCurrencyGuard(other);
        return this.value.compareTo(other.value) < 0;
    }

    private void sameCurrencyGuard(Price other) {
        if (!this.currency.equals(other.currency))
            throw new IllegalArgumentException(
                "Cannot compare prices in different currencies: " + this.currency + " vs " + other.currency);
    }

    @Override
    public String toString() {
        return currency + " " + value.stripTrailingZeros().toPlainString();
    }
}
