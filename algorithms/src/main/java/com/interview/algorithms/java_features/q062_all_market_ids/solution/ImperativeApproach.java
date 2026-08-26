package com.interview.algorithms.java_features.q062_all_market_ids.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

// Trade-off vs Solution.java: flatMap's "flatten as you go" behaviour is replaced here by an
// explicit nested loop (outer over slips, inner over each slip's selections) — two levels of
// iteration made visible instead of one flatMap call hiding them. TreeSet does double duty as
// both the dedup mechanism (distinct()'s imperative equivalent) and the sort (sorted()'s
// imperative equivalent), since a TreeSet is sorted-and-unique by construction — trading one
// stream pipeline for "pick a collection whose invariants happen to match what you need."
public class ImperativeApproach {

    public static List<String> allMarketIdsSortedDistinct(List<BetSlip> slips) {
        TreeSet<String> marketIds = new TreeSet<>();
        for (BetSlip slip : slips) {
            for (Selection selection : slip.selections()) {
                marketIds.add(selection.marketId());
            }
        }
        return new ArrayList<>(marketIds);
    }
}
