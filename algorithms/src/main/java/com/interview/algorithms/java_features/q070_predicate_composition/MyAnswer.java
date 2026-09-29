package com.interview.algorithms.java_features.q070_predicate_composition;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

public class MyAnswer {

    public record Bet(String betRef, String bettor, BigDecimal stake, String region, boolean selfExcluded) {}

    // Rules return true = bet is SAFE. Combine with .and() — bet approved only if ALL rules pass.
    public static List<Bet> approvedBets(List<Bet> bets, List<Predicate<Bet>> rules) {
        // TODO implement
        throw new UnsupportedOperationException("implement approvedBets");
    }

    // Rules return true = bet is a RISK. Combine with .or() — bet blocked if ANY rule fires.
    public static List<Bet> blockedBets(List<Bet> bets, List<Predicate<Bet>> rules) {
        // TODO implement
        throw new UnsupportedOperationException("implement blockedBets");
    }
}
