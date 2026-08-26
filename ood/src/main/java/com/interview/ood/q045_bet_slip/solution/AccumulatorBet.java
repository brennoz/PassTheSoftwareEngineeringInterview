package com.interview.ood.q045_bet_slip.solution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// Pattern: Composite — but note this composes Selection value objects, not Wager. legs is
// deliberately List<Selection>, NOT List<Wager>, so "an accumulator leg is independently
// staked" becomes a type error instead of a runtime rule someone has to remember to enforce
// with an instanceof guard. See Selection's own comment for why the alternative (Selection
// implements Wager) was rejected.
// Alternative rejected: letting an empty accumulator report isWon() == true (vacuous truth
// of allMatch on an empty stream) — rejected because a zero-leg slip paying out
// stake*1 on settlement is a real production bug shape, not a hypothetical; the guard below
// excludes the empty case explicitly.
public class AccumulatorBet implements Wager {

    private final List<Selection> legs = new ArrayList<>();
    private final BigDecimal stake;

    public AccumulatorBet(BigDecimal stake) {
        this.stake = stake;
    }

    public void addLeg(Selection selection) {
        legs.add(selection);
    }

    @Override
    public BigDecimal getStake() {
        return stake;
    }

    @Override
    public boolean isWon() {
        return !legs.isEmpty() && legs.stream().allMatch(leg -> leg.getResult() == SelectionResult.WON);
    }

    // Why combinedOdds is recomputed each call instead of cached: legs don't change once
    // settlement begins in this design, and reduce() over 2-6 legs is not a hot path worth
    // memoizing — caching here would be optimizing a cost that doesn't exist yet.
    // What NOT to do: don't multiply odds unconditionally before checking isWon() — a still
    // PENDING or partially LOST accumulator must return ZERO, not a combined-odds figure
    // for a bet that hasn't actually won.
    @Override
    public BigDecimal calculatePayout() {
        if (!isWon()) {
            return BigDecimal.ZERO;
        }
        BigDecimal combinedOdds = legs.stream()
                .map(Selection::getOdds)
                .reduce(BigDecimal.ONE, BigDecimal::multiply);
        return stake.multiply(combinedOdds);
    }
}
