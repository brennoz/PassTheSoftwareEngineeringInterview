package com.interview.pairingdrill.q078_payment_search.answer;

import java.math.BigDecimal;

public record SearchCriteria (BigDecimal minAmount, BigDecimal maxAmount, PaymentStatus status, PaymentType type) {

    public static SearchCriteria empty() {
        return new SearchCriteria(null, null, null, null);
    }
}
