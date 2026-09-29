package com.interview.pairingdrill.q077_market_settlement.solution;

// Why enum not String: the original incident was caused by "home" vs "HOME" — a
// case-sensitive String comparison that compiled fine and silently produced zero winning
// bets. The same class of bug exists for market status: nothing prevents calling
// setStatus("settled") or setStatus("Settled") — both are valid Strings, both silently
// break every downstream status check. An enum makes the invalid value unrepresentable.
public enum MarketStatus {
    OPEN, SUSPENDED, SETTLED
}
