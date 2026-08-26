package com.interview.algorithms.java_features.q064_suspended_markets_summary.solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Trade-off vs Solution.java: StringBuilder needs an explicit "is this the first element"
// check to avoid a leading/dangling delimiter — Collectors.joining handles that internally,
// only ever placing the delimiter BETWEEN elements, never before the first or after the last.
// Getting that boundary condition right by hand is the single most common way to introduce an
// off-by-one bug in a manual join implementation.
public class ImperativeApproach {

    public static String suspendedMarketsSummary(List<Market> markets) {
        List<String> suspendedIds = new ArrayList<>();
        for (Market market : markets) {
            if (market.status() == MarketStatus.SUSPENDED) {
                suspendedIds.add(market.marketId());
            }
        }
        Collections.sort(suspendedIds);

        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < suspendedIds.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(suspendedIds.get(i));
        }
        builder.append("]");
        return builder.toString();
    }
}
