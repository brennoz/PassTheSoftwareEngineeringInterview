package com.interview.algorithms.java_features.q064_suspended_markets_summary.solution;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    // What this verifies: only SUSPENDED markets appear, ACTIVE and CLOSED are excluded, and
    // the output is wrapped exactly in "[" / "]" with ", " between elements — not just
    // "contains the right substrings," but the exact expected string.
    @Test
    void includesOnlySuspendedMarketsWrappedAndCommaSeparated() {
        List<Market> markets = List.of(
                new Market("match-303", MarketStatus.SUSPENDED),
                new Market("match-101", MarketStatus.ACTIVE),
                new Market("match-202", MarketStatus.SUSPENDED),
                new Market("match-404", MarketStatus.CLOSED)
        );

        String result = Solution.suspendedMarketsSummary(markets);

        assertEquals("[match-202, match-303]", result);
    }

    // What this verifies: the empty case produces "[]" cleanly via Collectors.joining's
    // prefix/suffix handling — not a dangling "[" from a hand-rolled StringBuilder that only
    // appends the closing bracket when there was at least one element.
    @Test
    void noSuspendedMarketsProducesEmptyBrackets() {
        List<Market> markets = List.of(new Market("match-101", MarketStatus.ACTIVE));

        String result = Solution.suspendedMarketsSummary(markets);

        assertEquals("[]", result);
    }

    @Test
    void functionalAndImperativeApproachesAgreeOnResults() {
        List<Market> markets = List.of(
                new Market("match-303", MarketStatus.SUSPENDED),
                new Market("match-101", MarketStatus.ACTIVE),
                new Market("match-202", MarketStatus.SUSPENDED),
                new Market("match-404", MarketStatus.CLOSED)
        );

        assertEquals(
                ImperativeApproach.suspendedMarketsSummary(markets),
                Solution.suspendedMarketsSummary(markets)
        );
    }
}
