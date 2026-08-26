package com.interview.ood.q045_bet_slip.solution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// Pattern: Composite — the one node in this tree that genuinely needs recursion. It holds
// List<Wager>, and a Wager can be a SingleBet, an AccumulatorBet, or another BetSlip.
// Nesting BetSlip inside BetSlip is deliberate, unlike AccumulatorBet's flat
// List<Selection>: grouping a customer's wagers by session, then rolling several sessions
// into one account-level slip, is a real reporting need, and Composite is exactly the
// pattern that lets calculatePayout() recurse through however many grouping levels exist
// without this class knowing or caring how deep the tree goes.
// Alternative rejected: giving BetSlip a max-nesting guard the way AccumulatorBet restricts
// its legs to Selection — rejected here because there is no business rule against nesting
// slips (unlike the real "no accas of accas" bookmaker rule AccumulatorBet enforces
// structurally), so adding a guard would defend against a problem this domain doesn't have.
public class BetSlip implements Wager {

    private final List<Wager> wagers = new ArrayList<>();

    public void addWager(Wager wager) {
        wagers.add(wager);
    }

    @Override
    public BigDecimal getStake() {
        return wagers.stream()
                .map(Wager::getStake)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // What NOT to do: don't implement this as wagers.stream().anyMatch(Wager::isWon) —
    // "any child won" would report a slip as won when three wagers lost and only one won,
    // which is not how a slip's overall status should read to a customer or an auditor.
    @Override
    public boolean isWon() {
        return !wagers.isEmpty() && wagers.stream().allMatch(Wager::isWon);
    }

    @Override
    public BigDecimal calculatePayout() {
        return wagers.stream()
                .map(Wager::calculatePayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
