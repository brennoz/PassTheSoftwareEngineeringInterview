package com.interview.pairingdrill.q078_payment_search.answer;

import java.math.BigDecimal;

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
