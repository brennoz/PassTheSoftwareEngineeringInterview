package com.interview.pairingdrill.q080_live_odds_service.solution;

// Fix 6: typed enum instead of raw String — compiler-enforced valid states;
// "SUSPENDED".equals(status) comparisons are error-prone (typo silently compiles).
public enum MarketStatus {
    ACTIVE,
    SUSPENDED,
    SETTLED
}
