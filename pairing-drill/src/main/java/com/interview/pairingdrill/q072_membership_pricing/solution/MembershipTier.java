package com.interview.pairingdrill.q072_membership_pricing.solution;

import java.math.BigDecimal;

// Why: replaces a raw String tier + duplicated if/else fee lookup in both call sites with one
// place the fee lives — kills primitive obsession and the two-copies-of-the-price-table smell
// at once.
public enum MembershipTier {
    BASIC(new BigDecimal("250.00")),
    PREMIUM(new BigDecimal("750.00")),
    ENTERPRISE(new BigDecimal("2000.00"));

    private final BigDecimal fee;

    MembershipTier(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFee() {
        return fee;
    }
}
