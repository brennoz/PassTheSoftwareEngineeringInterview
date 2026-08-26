package com.interview.algorithms.java_features.q061_highest_stake_per_market.solution;

import java.math.BigDecimal;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Bet> bets = List.of(
                new Bet("b1", "match-101", new BigDecimal("10.00")),
                new Bet("b2", "match-101", new BigDecimal("25.00")),
                new Bet("b3", "match-202", new BigDecimal("5.00")),
                new Bet("b4", "match-101", new BigDecimal("15.00"))
        );

        System.out.println("Functional:  " + Solution.highestStakePerMarket(bets));
        System.out.println("Imperative:  " + ImperativeApproach.highestStakePerMarket(bets));
        System.out.println("Same result: " +
                Solution.highestStakePerMarket(bets).equals(ImperativeApproach.highestStakePerMarket(bets)));
        // match-101 should resolve to b2 (25.00) — the highest of b1/b2/b4
    }
}
