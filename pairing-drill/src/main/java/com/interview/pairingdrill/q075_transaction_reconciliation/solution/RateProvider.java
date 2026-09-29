package com.interview.pairingdrill.q075_transaction_reconciliation.solution;

import java.math.BigDecimal;
import java.util.Optional;

public interface RateProvider {

    // Why Optional<BigDecimal> in the signature, not a nullable BigDecimal: the seeded version
    // returned a plain BigDecimal that was sometimes null, with nothing in the method signature
    // warning callers. That forced every caller to either remember to null-check or crash.
    // Encoding "might not have a rate" in the return type makes the missing-rate case part of
    // the contract, not tribal knowledge.
    Optional<BigDecimal> getRate(String currencyCode);
}
