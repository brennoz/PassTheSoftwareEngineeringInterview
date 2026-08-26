package com.interview.algorithms.java_features.q065_total_stake_per_market.solution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    // What this verifies: stakes on the SAME market across different bets are summed, not
    // overwritten — a groupingBy without a proper downstream reducer (or one that just keeps
    // the last value) would fail this.
    @Test
    void sumsStakesForBetsOnTheSameMarket() {
        List<Bet> bets = List.of(
                new Bet("b1", "match-303", new BigDecimal("10.00")),
                new Bet("b2", "match-303", new BigDecimal("5.00"))
        );

        Map<String, BigDecimal> result = Solution.totalStakePerMarketSorted(bets);

        assertEquals(0, result.get("match-303").compareTo(new BigDecimal("15.00")));
    }

    // What this verifies: the returned map's KEY ORDER is sorted by marketId, even though the
    // input list's marketIds are not in sorted order — this is the actual point of supplying
    // TreeMap::new as groupingBy's map factory, not just "the right totals happen to be in
    // there somewhere."
    @Test
    void resultMapIsSortedByMarketIdRegardlessOfInputOrder() {
        List<Bet> bets = List.of(
                new Bet("b1", "match-303", new BigDecimal("10.00")),
                new Bet("b2", "match-101", new BigDecimal("25.00")),
                new Bet("b3", "match-202", new BigDecimal("15.00"))
        );

        Map<String, BigDecimal> result = Solution.totalStakePerMarketSorted(bets);

        assertEquals(List.of("match-101", "match-202", "match-303"), new ArrayList<>(result.keySet()));
    }

    @Test
    void functionalAndImperativeApproachesAgreeOnResults() {
        List<Bet> bets = List.of(
                new Bet("b1", "match-303", new BigDecimal("10.00")),
                new Bet("b2", "match-101", new BigDecimal("25.00")),
                new Bet("b3", "match-303", new BigDecimal("5.00")),
                new Bet("b4", "match-202", new BigDecimal("15.00"))
        );

        assertEquals(
                ImperativeApproach.totalStakePerMarketSorted(bets),
                Solution.totalStakePerMarketSorted(bets)
        );
    }
}
