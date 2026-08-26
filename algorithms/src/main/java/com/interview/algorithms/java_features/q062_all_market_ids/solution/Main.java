package com.interview.algorithms.java_features.q062_all_market_ids.solution;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<BetSlip> slips = List.of(
                new BetSlip("s1", List.of(
                        new Selection("match-101", "home-win"),
                        new Selection("match-202", "draw"))),
                new BetSlip("s2", List.of(
                        new Selection("match-101", "home-win"),   // duplicate marketId across slips
                        new Selection("match-303", "away-win")))
        );

        System.out.println("Functional:  " + Solution.allMarketIdsSortedDistinct(slips));
        System.out.println("Imperative:  " + ImperativeApproach.allMarketIdsSortedDistinct(slips));
        System.out.println("Same result: " +
                Solution.allMarketIdsSortedDistinct(slips).equals(ImperativeApproach.allMarketIdsSortedDistinct(slips)));
        // expect: [match-101, match-202, match-303]
    }
}
