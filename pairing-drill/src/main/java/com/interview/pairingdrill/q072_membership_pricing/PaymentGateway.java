package com.interview.pairingdrill.q072_membership_pricing;

import java.math.BigDecimal;

public interface PaymentGateway {

    boolean charge(String cardToken, BigDecimal amount, String idempotencyKey);
}
