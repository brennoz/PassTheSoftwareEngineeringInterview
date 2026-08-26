package com.interview.algorithms.java_features.q062_all_market_ids.solution;

import java.util.List;

public class Solution {

    // flatMap, not map: mapping each BetSlip to its List<Selection> would give a
    // Stream<List<Selection>> — one element per slip, each element still a whole list. flatMap
    // takes the function's List<Selection> result and splices its elements directly into the
    // outer stream instead of nesting it, so the pipeline downstream (map/distinct/sorted)
    // operates on individual Selections across ALL slips, not slip-by-slip. What NOT to do:
    // .map(BetSlip::selections).flatMap(List::stream) also works but is two stream ops where
    // one flatMap(slip -> slip.selections().stream()) does the same job in one step.
    //
    // distinct() before sorted(): order doesn't matter for correctness here (dedup is order-
    // independent), but distinct() first means sorted() has fewer elements to compare — a
    // detail, not a correctness requirement, but worth naming since reversing the two calls
    // would still be correct, just marginally less efficient on data with many duplicates.
    public static List<String> allMarketIdsSortedDistinct(List<BetSlip> slips) {
        return slips.stream()
                .flatMap(slip -> slip.selections().stream())
                .map(Selection::marketId)
                .distinct()
                .sorted()
                .toList();
    }
}
