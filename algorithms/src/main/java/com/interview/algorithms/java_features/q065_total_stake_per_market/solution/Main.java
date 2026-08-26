package com.interview.algorithms.java_features.q065_total_stake_per_market.solution;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Bet> bets = List.of(
                new Bet("b1", "match-303", new BigDecimal("10.00")),
                new Bet("b2", "match-101", new BigDecimal("25.00")),
                new Bet("b3", "match-303", new BigDecimal("5.00")),
                new Bet("b4", "match-202", new BigDecimal("15.00"))
        );

        System.out.println("Functional (sorted by marketId):  " + Solution.totalStakePerMarketSorted(bets));
        System.out.println("Imperative (sorted by marketId):  " + ImperativeApproach.totalStakePerMarketSorted(bets));
        // note the map keys print in sorted order (match-101, match-202, match-303) even though
        // the input list's marketIds appear in a different order (303, 101, 303, 202)
    }
}
