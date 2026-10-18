package com.interview.pairingdrill.q078_payment_search.solution;

import java.math.BigDecimal;

// Why null-safe compareTo rather than NPE-risking compareTo directly:
// min and max are both optional. A caller may provide only a floor, only a ceiling,
// or both. Null-checking each independently is the correct pattern.
// What NOT to do: use a sentinel value (0.0) to mean "not set" as the seeded code
// did — that makes it impossible to search for payments of exactly £0.00 and is
// a hidden semantic landmine.
public class AmountRangeSpec implements PaymentSpec {

    private final BigDecimal min;
    private final BigDecimal max;

    public AmountRangeSpec(BigDecimal min, BigDecimal max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isSatisfiedBy(Payment p) {
        if (min != null && p.getAmount().compareTo(min) < 0) return false;
        if (max != null && p.getAmount().compareTo(max) > 0) return false;
        return true;
    }
}
