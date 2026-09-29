package com.interview.pairingdrill.q077_market_settlement.solution;

// Why enum not String: the incident was a caller passing "home" instead of "HOME".
// String.equals() is case-sensitive and silent on mismatch — no exception, no warning,
// just zero winning bets and a full payout of zero sent to customers. With BettingSide,
// the compiler rejects any value that isn't HOME, AWAY, or DRAW at the call site.
// What NOT to do: keep the String param and add an upstream .toUpperCase() — that just
// moves the smell one layer up and still lets "HOMM" or "" through silently.
public enum BettingSide {
    HOME, AWAY, DRAW
}
