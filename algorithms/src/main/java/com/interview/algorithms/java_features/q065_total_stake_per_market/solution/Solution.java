package com.interview.algorithms.java_features.q065_total_stake_per_market.solution;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Solution {

    // groupingBy's 3-arg overload — (classifier, mapFactory, downstream) — is different from
    // the 2-arg and the other 3-arg (classifier, downstream) overloads used elsewhere: this one
    // controls WHICH Map implementation backs the result, not just how each group's values get
    // reduced. Supplying TreeMap::new means the result is sorted by key (marketId) by
    // construction — a property of the Map itself, not something applied afterward by sorting
    // entries into a List. The default (2-arg groupingBy, or the classifier+downstream 3-arg
    // overload) backs the result with a HashMap, whose iteration order is unspecified — it may
    // look consistent for a given JVM/run but is not a guarantee to build behaviour on.
    //
    // reducing(BigDecimal.ZERO, Bet::stake, BigDecimal::add) as the downstream: same shape used
    // for a per-department total in an earlier exercise — BigDecimal has no dedicated
    // summingBigDecimal collector the way summingInt/summingLong/summingDouble exist for
    // primitives, and summingDouble isn't an option under this project's BigDecimal-for-money
    // rule, so reducing() is the correct tool here regardless of which map factory is in play.
    public static Map<String, BigDecimal> totalStakePerMarketSorted(List<Bet> bets) {
        return bets.stream()
                .collect(Collectors.groupingBy(
                        Bet::marketId,
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Bet::stake, BigDecimal::add)
                ));
    }
}
