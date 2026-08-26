package com.interview.algorithms.java_features.q064_suspended_markets_summary;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MyAnswer {

    public static String suspendedMarketsSummary(List<Market> markets) {
        return markets.stream()
            .filter(market -> !market.status().equals(MarketStatus.SUSPENDED))
            .map(Market::marketId)
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.joining(", ", "[", "]"));
    }
}
