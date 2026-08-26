package com.interview.algorithms.java_features.q064_suspended_markets_summary.solution;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // Collectors.joining(delimiter, prefix, suffix) — the 3-arg overload — wraps the whole
    // joined string once, not per-element; the 1-arg overload (delimiter only) would need a
    // separate string-concat step afterward to add "[" and "]", which is exactly the kind of
    // manual bookkeeping the 3-arg form exists to remove. Also worth knowing: joining() handles
    // the empty-stream case for you — zero suspended markets correctly produces "[]", not "["
    // with a dangling missing "]" or a thrown exception, because prefix+suffix are appended
    // around the (possibly empty) joined body regardless of element count.
    //
    // sorted() before collecting: without it, output order follows stream encounter order,
    // which for a List source is insertion order — deterministic, but not necessarily what a
    // human reading a summary string wants ("[m3, m1, m2]" reads worse than "[m1, m2, m3]" and
    // isn't diffable run-to-run if the input list's order isn't itself stable upstream).
    public static String suspendedMarketsSummary(List<Market> markets) {
        return markets.stream()
                .filter(market -> market.status() == MarketStatus.SUSPENDED)
                .map(Market::marketId)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
