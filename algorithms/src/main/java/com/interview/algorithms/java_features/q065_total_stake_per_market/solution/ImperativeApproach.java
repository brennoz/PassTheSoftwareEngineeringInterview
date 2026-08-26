package com.interview.algorithms.java_features.q065_total_stake_per_market.solution;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// Trade-off vs Solution.java: choosing TreeMap as the accumulator here is the imperative
// equivalent of groupingBy's mapFactory argument — same idea, just constructed directly
// instead of handed to a collector. The real difference is the accumulation step itself:
// merge()/compute() collapses "look up existing total, or start from zero, then add" into one
// call; without it, the get-or-default-then-put dance has to be written out by hand for every
// entry.
public class ImperativeApproach {

    public static Map<String, BigDecimal> totalStakePerMarketSorted(List<Bet> bets) {
        Map<String, BigDecimal> totals = new TreeMap<>();
        for (Bet bet : bets) {
            totals.merge(bet.marketId(), bet.stake(), BigDecimal::add);
        }
        return totals;
    }
}
