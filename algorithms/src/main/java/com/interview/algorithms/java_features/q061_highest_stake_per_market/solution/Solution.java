package com.interview.algorithms.java_features.q061_highest_stake_per_market.solution;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Solution {

    // Collectors.toMap's 3-arg overload (keyMapper, valueMapper, mergeFunction): the 2-arg
    // overload throws IllegalStateException the moment it hits a second entry for a key it's
    // already seen — fine for data known to be unique, wrong here since multiple bets on the
    // same market are the expected, common case, not an error condition. The merge function is
    // what decides "keep A or B" when a collision happens; here that's BinaryOperator.maxBy —
    // it delegates to a Comparator, and returns whichever of the two arguments compares
    // greater, without you writing the if/else yourself.
    //
    // Different from ConcurrentHashMap.merge() (q051): that merge function resolves concurrent
    // writes into a live shared map at runtime — this one resolves colliding keys while
    // building a single map from a stream in one pass, single-threaded. Same shape (a
    // BiFunction<V,V,V> deciding what happens on collision), different problem each solves.
    public static Map<String, Bet> highestStakePerMarket(List<Bet> bets) {
        BinaryOperator<Bet> keepHigherStake = BinaryOperator.maxBy(Comparator.comparing(Bet::stake));

        return bets.stream()
                .collect(Collectors.toMap(Bet::marketId, Function.identity(), keepHigherStake));
    }
}
