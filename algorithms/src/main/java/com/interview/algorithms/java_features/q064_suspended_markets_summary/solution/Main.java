package com.interview.algorithms.java_features.q064_suspended_markets_summary.solution;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Market> markets = List.of(
                new Market("match-303", MarketStatus.SUSPENDED),
                new Market("match-101", MarketStatus.ACTIVE),
                new Market("match-202", MarketStatus.SUSPENDED),
                new Market("match-404", MarketStatus.CLOSED)
        );

        System.out.println("Functional:  " + Solution.suspendedMarketsSummary(markets));
        System.out.println("Imperative:  " + ImperativeApproach.suspendedMarketsSummary(markets));

        System.out.println("No suspended markets, functional:  " +
                Solution.suspendedMarketsSummary(List.of(new Market("match-101", MarketStatus.ACTIVE))));
    }
}
