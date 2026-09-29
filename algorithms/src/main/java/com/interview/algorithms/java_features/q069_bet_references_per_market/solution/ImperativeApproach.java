package com.interview.algorithms.java_features.q069_bet_references_per_market.solution;

import com.interview.algorithms.java_features.q069_bet_references_per_market.MyAnswer.Bet;

import java.util.*;

public class ImperativeApproach {

    // computeIfAbsent: creates the accumulator list only on first encounter of a market.
    // Why it beats get + putIfAbsent: putIfAbsent(k, new ArrayList<>()) always allocates
    // a new list — the allocation is unconditional, just discarded when the key exists.
    // computeIfAbsent calls the factory only when k is absent, zero waste on repeated keys.
    //
    // The sort step runs after all bets are accumulated — same ordering guarantee as the
    // functional version's collectingAndThen finisher.
    public static Map<String, List<String>> betRefsByMarket(List<Bet> bets) {
        Map<String, List<String>> result = new HashMap<>();
        for (Bet bet : bets) {
            result.computeIfAbsent(bet.marketId(), k -> new ArrayList<>())
                  .add(bet.betRef());
        }
        result.values().forEach(Collections::sort);
        return result;
    }
}
