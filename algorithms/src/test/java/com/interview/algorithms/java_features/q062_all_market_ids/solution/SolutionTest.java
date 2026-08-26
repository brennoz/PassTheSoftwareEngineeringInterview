package com.interview.algorithms.java_features.q062_all_market_ids.solution;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    // What this verifies: selections nested inside DIFFERENT slips get flattened into one
    // stream together — a .map(BetSlip::selections) without flatMap would produce a
    // Stream<List<Selection>>, one element per slip, and this assertion on individual market
    // IDs across slips would never compile/typecheck against that shape in the first place.
    @Test
    void flattensSelectionsAcrossAllSlips() {
        List<BetSlip> slips = List.of(
                new BetSlip("s1", List.of(new Selection("match-303", "away-win"))),
                new BetSlip("s2", List.of(new Selection("match-101", "home-win")))
        );

        List<String> result = Solution.allMarketIdsSortedDistinct(slips);

        assertEquals(List.of("match-101", "match-303"), result);
    }

    // What this verifies: the same marketId appearing in two different slips' selections
    // collapses to one entry — distinct() must run on the flattened Selection stream, not
    // per-slip (which would leave duplicates whenever the same market is bet on twice).
    @Test
    void duplicateMarketIdsAcrossSlipsCollapseToOne() {
        List<BetSlip> slips = List.of(
                new BetSlip("s1", List.of(
                        new Selection("match-101", "home-win"),
                        new Selection("match-202", "draw"))),
                new BetSlip("s2", List.of(new Selection("match-101", "home-win")))
        );

        List<String> result = Solution.allMarketIdsSortedDistinct(slips);

        assertEquals(List.of("match-101", "match-202"), result);
    }

    @Test
    void aSlipWithNoSelectionsContributesNothing() {
        List<BetSlip> slips = List.of(
                new BetSlip("s1", List.of()),
                new BetSlip("s2", List.of(new Selection("match-101", "home-win")))
        );

        List<String> result = Solution.allMarketIdsSortedDistinct(slips);

        assertEquals(List.of("match-101"), result);
    }

    @Test
    void functionalAndImperativeApproachesAgreeOnResults() {
        List<BetSlip> slips = List.of(
                new BetSlip("s1", List.of(
                        new Selection("match-101", "home-win"),
                        new Selection("match-202", "draw"))),
                new BetSlip("s2", List.of(
                        new Selection("match-101", "home-win"),
                        new Selection("match-303", "away-win")))
        );

        assertEquals(
                ImperativeApproach.allMarketIdsSortedDistinct(slips),
                Solution.allMarketIdsSortedDistinct(slips)
        );
    }
}
