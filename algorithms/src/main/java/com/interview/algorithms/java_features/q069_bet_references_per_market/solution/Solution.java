package com.interview.algorithms.java_features.q069_bet_references_per_market.solution;

import com.interview.algorithms.java_features.q069_bet_references_per_market.MyAnswer.Bet;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

public class Solution {

    // Collectors.mapping — extracts betRef from each Bet before the downstream toList sees it.
    // collectingAndThen wraps the whole downstream: its finisher runs once, after the group is
    // fully accumulated, sorting the collected list in-place. This is the correct hook for
    // per-group ordering — a .sorted() in the outer stream pipeline would sort globally,
    // not per-group.
    //
    // Alternative rejected: groupingBy(Bet::marketId) → Map<String, List<Bet>>, then a second
    // stream over byMarket.entrySet() to transform values. Two passes, more allocations, harder
    // to read — use mapping to stay in one terminal collect call.
    public static Map<String, List<String>> betRefsByMarket(List<Bet> bets) {
        return bets.stream()
                .collect(groupingBy(
                        Bet::marketId,
                        collectingAndThen(
                                mapping(Bet::betRef, toList()),
                                refs -> refs.stream().sorted().toList()
                        )
                ));
    }
}
