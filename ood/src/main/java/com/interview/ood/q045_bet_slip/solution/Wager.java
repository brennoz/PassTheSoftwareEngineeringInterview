package com.interview.ood.q045_bet_slip.solution;

import java.math.BigDecimal;

// Pattern: Composite — the shared interface every node in a bet slip tree implements, so
// BetSlip can sum stake/payout over SingleBet, AccumulatorBet, and nested BetSlip without a
// switch/instanceof per concrete type.
// Alternative rejected: a single "Bet" class with a nullable List<Bet> children field
// (making every node implicitly composite-capable) — rejected because it lets a SingleBet
// accidentally carry children, silently turning a leaf into a container with no
// compile-time signal that anything has gone wrong.
// What NOT to do: don't add a getChildren() method here just because BetSlip needs one —
// that would force SingleBet and AccumulatorBet to return an empty list forever, leaking a
// composite-specific concern into every leaf that has nothing to do with children.
public interface Wager {
    BigDecimal getStake();
    BigDecimal calculatePayout();
    boolean isWon();
}
