package com.interview.pairingdrill.q077_market_settlement.solution;

import java.math.BigDecimal;

// Why record not class: Stake is a value object — it has no identity beyond its data,
// and there is no valid reason to mutate a stake after creation (what would it mean to
// change the amount of an already-placed bet?). The seeded class had setters, which means
// a caller could silently change the amount mid-calculation. A record is immutable by
// construction: no setters, final fields, equals/hashCode/toString generated correctly.
//
// Why BigDecimal not double: the seeded code used double for amount. 1.75 * 10.0 in
// floating-point can produce 17.499999999... rather than exactly 17.50. For a payout
// that lands in a customer's wallet, that's a regulatory and financial liability. BigDecimal
// is exact for decimal arithmetic at any scale.
//
// Accessor syntax: record fields are accessed via amount() not getAmount(). This is the
// canonical Java record convention — not a typo.
public record Stake(BigDecimal amount, String currency) {
}
