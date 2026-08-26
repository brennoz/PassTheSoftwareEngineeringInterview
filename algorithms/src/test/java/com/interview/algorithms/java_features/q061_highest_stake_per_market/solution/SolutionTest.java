package com.interview.algorithms.java_features.q061_highest_stake_per_market.solution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    // What this verifies: when multiple bets collide on the same marketId, the merge function
    // actually runs and picks the higher stake — not just "first one wins" or "last one wins",
    // which a naive Collectors.toMap(keyMapper, valueMapper) (no merge function) would throw on,
    // and a wrong merge function (e.g. always keeping the second argument) could pass by
    // accident depending on input order.
    @Test
    void keepsTheHigherStakeWhenTwoBetsShareAMarket() {
        List<Bet> bets = List.of(
                new Bet("b1", "match-101", new BigDecimal("10.00")),
                new Bet("b2", "match-101", new BigDecimal("25.00"))
        );

        Map<String, Bet> result = Solution.highestStakePerMarket(bets);

        assertEquals("b2", result.get("match-101").id());
    }

    // What this verifies: the winner is genuinely "highest stake," not "whichever bet appears
    // later/earlier in the input list" — the higher-stake bet is placed FIRST here, so a merge
    // function that silently keeps whichever argument is passed second (order-dependent instead
    // of value-dependent) would fail this specific ordering.
    @Test
    void higherStakeWinsRegardlessOfInputOrder() {
        List<Bet> bets = List.of(
                new Bet("b1", "match-101", new BigDecimal("25.00")),
                new Bet("b2", "match-101", new BigDecimal("10.00"))
        );

        Map<String, Bet> result = Solution.highestStakePerMarket(bets);

        assertEquals("b1", result.get("match-101").id());
    }

    @Test
    void marketsWithASingleBetAreUnaffected() {
        List<Bet> bets = List.of(new Bet("b1", "match-202", new BigDecimal("5.00")));

        Map<String, Bet> result = Solution.highestStakePerMarket(bets);

        assertEquals(1, result.size());
        assertEquals("b1", result.get("match-202").id());
    }

    @Test
    void functionalAndImperativeApproachesAgreeOnResults() {
        List<Bet> bets = List.of(
                new Bet("b1", "match-101", new BigDecimal("10.00")),
                new Bet("b2", "match-101", new BigDecimal("25.00")),
                new Bet("b3", "match-202", new BigDecimal("5.00")),
                new Bet("b4", "match-101", new BigDecimal("15.00"))
        );

        assertEquals(
                ImperativeApproach.highestStakePerMarket(bets),
                Solution.highestStakePerMarket(bets)
        );
    }
}
