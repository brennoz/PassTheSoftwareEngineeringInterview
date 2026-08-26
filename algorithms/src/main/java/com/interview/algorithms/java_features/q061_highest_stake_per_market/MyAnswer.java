package com.interview.algorithms.java_features.q061_highest_stake_per_market;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MyAnswer {

    public static Map<String, Bet> highestStakePerMarket(List<Bet> bets) {
        return bets.stream()
                .collect(Collectors.toMap(Bet::marketId, 
                                          Function.identity(), 
                                          BinaryOperator.maxBy(Comparator.comparing(Bet::stake))));
                                          
    }
}
