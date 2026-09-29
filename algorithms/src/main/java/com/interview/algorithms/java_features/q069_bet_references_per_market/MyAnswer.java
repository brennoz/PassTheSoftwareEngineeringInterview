package com.interview.algorithms.java_features.q069_bet_references_per_market;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyAnswer {

    public record Bet(String betRef, String marketId, String bettor, BigDecimal stake) {}

    public static Map<String, List<String>> betRefsByMarket(List<Bet> bets) {
        return bets.stream()
                .collect(Collectors.groupingBy(
                    Bet::marketId,
                    Collectors.collectingAndThen(
                        Collectors.mapping(Bet::betRef, Collectors.toList()),
                        refs -> refs.stream().sorted().toList()
                    )
                ));
    }
}
