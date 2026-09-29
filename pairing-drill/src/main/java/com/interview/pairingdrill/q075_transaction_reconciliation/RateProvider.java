package com.interview.pairingdrill.q075_transaction_reconciliation;

import java.math.BigDecimal;

public interface RateProvider {

    // Returns null for a currency it doesn't recognise — nothing in this signature says so.
    BigDecimal getRate(String currencyCode);
}
