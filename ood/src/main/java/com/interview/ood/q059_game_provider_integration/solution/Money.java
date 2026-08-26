package com.interview.ood.q059_game_provider_integration.solution;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

// Pattern: Value Object — immutable, non-negative by construction. Kept self-contained in
// this question rather than shared with q037's Money: OOD questions in this project are
// independent modules, and pulling a cross-question dependency for one record isn't worth
// the coupling.
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "amount required");
        Objects.requireNonNull(currency, "currency required");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
    }
}
