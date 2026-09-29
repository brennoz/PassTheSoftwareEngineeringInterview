package com.interview.pairingdrill.q072_membership_pricing.solution;

import java.math.BigDecimal;

public interface PaymentGateway {

    // Why BigDecimal not double: this project's own domain rubric flags double/float for
    // monetary amounts by name — floating point loses cents at scale and this is a real charge
    // amount, not a display value.
    boolean charge(String cardToken, BigDecimal amount);
}
