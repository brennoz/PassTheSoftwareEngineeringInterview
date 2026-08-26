package com.interview.algorithms.java_features.q060_top_clean_transactions.solution;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    // Collectors.partitioningBy: exactly 2 output buckets (true/false), both keys always
    // present even if empty — unlike groupingBy, no need to guard against a missing key with
    // getOrDefault. That's the right tool here specifically because "flagged" is a boolean,
    // not an open set of categories; groupingBy would work too but partitioningBy signals the
    // binary intent directly and skips the Optional-shaped uncertainty about which keys exist.
    //
    // thenComparing(id) as tiebreaker: two transactions can tie on amount. Without a
    // deterministic tiebreaker, sorted() on equal-amount elements has undefined relative order
    // per the Comparator contract (though most JDK sorts happen to be stable) — an interviewer
    // asking "is this order guaranteed?" is testing exactly this gap.
    public static List<Transaction> topCleanTransactions(List<Transaction> transactions, int topN) {
        Map<Boolean, List<Transaction>> partitioned = transactions.stream()
                .collect(Collectors.partitioningBy(Transaction::flaggedForReview));

        return partitioned.get(false).stream()
                .sorted(Comparator.comparing(Transaction::amount).reversed()
                        .thenComparing(Transaction::id))
                .limit(topN)
                .collect(Collectors.toList());
    }
}
