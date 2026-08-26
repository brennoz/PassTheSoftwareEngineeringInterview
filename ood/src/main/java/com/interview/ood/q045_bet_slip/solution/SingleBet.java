package com.interview.ood.q045_bet_slip.solution;

import java.math.BigDecimal;

// Pattern: Composite (Leaf) — the simplest Wager: one Selection, one stake, no children.
// Alternative rejected: folding Selection's fields (eventName/odds/result) directly into
// SingleBet instead of composing a Selection object — rejected because AccumulatorBet needs
// the exact same shape for each of its legs, and duplicating those fields into two classes
// means a future addition (e.g. void/dead-heat handling) has to be added twice and kept in
// sync by hand.
public class SingleBet implements Wager {

    private final Selection selection;
    private final BigDecimal stake;

    public SingleBet(Selection selection, BigDecimal stake) {
        this.selection = selection;
        this.stake = stake;
    }

    @Override
    public BigDecimal getStake() {
        return stake;
    }

    @Override
    public boolean isWon() {
        return selection.getResult() == SelectionResult.WON;
    }

    // What NOT to do: don't return stake.multiply(odds) unconditionally and expect the
    // caller to check isWon() separately — a caller that forgets that check would pay out a
    // PENDING or LOST selection. calculatePayout() must be self-contained and safe to sum
    // blindly, since BetSlip.calculatePayout() does exactly that across a whole tree of
    // children it doesn't inspect individually.
    @Override
    public BigDecimal calculatePayout() {
        return isWon() ? stake.multiply(selection.getOdds()) : BigDecimal.ZERO;
    }
}
