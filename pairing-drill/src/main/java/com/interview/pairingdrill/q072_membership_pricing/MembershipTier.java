package com.interview.pairingdrill.q072_membership_pricing;

import java.math.BigDecimal;

public enum MembershipTier {
    BASIC(new BigDecimal("250.00")),
    PREMIUM(new BigDecimal("750.00")),
    ENTERPRISE(new BigDecimal("2000.00"));

    private final BigDecimal fee;

    MembershipTier(BigDecimal fee) { this.fee = fee; }

    public BigDecimal getFee() { return fee; }
}