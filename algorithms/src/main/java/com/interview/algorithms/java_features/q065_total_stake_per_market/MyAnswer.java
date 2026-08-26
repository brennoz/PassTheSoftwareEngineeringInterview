package com.interview.algorithms.java_features.q065_total_stake_per_market;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MyAnswer {

    public static Map<String, BigDecimal> totalStakePerMarketSorted(List<Bet> bets) {
        return bets.stream()
            .collect(Collectors.groupingBy(Bet::marketId,
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Bet::stake, BigDecimal::add))
                    );
    }
}
