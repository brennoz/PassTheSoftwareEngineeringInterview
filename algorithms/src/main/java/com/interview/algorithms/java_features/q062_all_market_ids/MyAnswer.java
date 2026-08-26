package com.interview.algorithms.java_features.q062_all_market_ids;

import java.util.List;

public class MyAnswer {

    public static List<String> allMarketIdsSortedDistinct(List<BetSlip> slips) {
        return slips.stream()
                    .flatMap(slip -> slip.selections().stream())
                    .map(selection -> selection.marketId())
                    .distinct()
                    .sorted()
                    .toList();
                    
    }
}
