package com.interview.ood.q037_payment_gateway.solution;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

// Pattern: Value Object — immutable, equality by value (record gives this for free), no
// identity of its own. Currency is part of the value, not a separate parameter threaded
// everywhere, so "add £10 + $10" is a programming error caught here, not downstream.
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "Amount is required");
        Objects.requireNonNull(currency, "Currency is required");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amonut must be non negative");
        }
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);
        BigDecimal result = amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient amount");
        }
        return new Money(result, currency);
    }

    private void assertSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
    }
}
