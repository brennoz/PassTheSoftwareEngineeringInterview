package com.interview.pairingdrill.q075_transaction_reconciliation.answer;

import java.math.BigDecimal;
import java.util.Optional;

public interface RateProvider {

    Optional<BigDecimal> getRate(String currencyCode);
}
