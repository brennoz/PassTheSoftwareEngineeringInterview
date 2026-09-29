package com.interview.algorithms.java_features.q070_predicate_composition.solution;

import com.interview.algorithms.java_features.q070_predicate_composition.MyAnswer.Bet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    private static Bet bet(String ref, String bettor, double stake, String region, boolean selfExcluded) {
        return new Bet(ref, bettor, BigDecimal.valueOf(stake), region, selfExcluded);
    }

    // Verifies that ALL rules must pass — a bet failing even one rule is excluded.
    // The rule ordering is deliberate: alice fails rule 2 (not rule 1), bob fails rule 1 (not rule 2).
    // This rules out an implementation that only checks the first or last rule.
    @Test
    void approved_bets_excludes_bets_failing_any_rule() {
        Bet alice = bet("B001", "alice",  50.00, "GB", true);  // self-excluded → fails notSelfExcluded
        Bet bob   = bet("B002", "bob",   200.00, "GB", false); // stake ≥ 100 → fails stakeUnder100
        Bet carol = bet("B003", "carol",  75.00, "GB", false); // stake < 100 ✓, not self-excluded ✓

        Predicate<Bet> stakeUnder100  = b -> b.stake().compareTo(BigDecimal.valueOf(100)) < 0;
        Predicate<Bet> notSelfExcluded = b -> !b.selfExcluded();

        List<Bet> result = Solution.approvedBets(
                List.of(alice, bob, carol),
                List.of(stakeUnder100, notSelfExcluded)
        );

        assertEquals(1, result.size());
        assertEquals("B003", result.get(0).betRef());
    }

    // Empty rules → identity predicate (always true) → all bets approved.
    // If implementation uses reduce(Predicate::and) without an identity, this would return
    // Optional.empty() and blow up on .filter() — the identity is load-bearing.
    @Test
    void approved_bets_returns_all_when_no_rules() {
        List<Bet> bets = List.of(
                bet("B001", "alice", 500.00, "GB", true),
                bet("B002", "bob",   999.00, "US", false)
        );

        List<Bet> result = Solution.approvedBets(bets, List.of());

        assertEquals(2, result.size());
    }

    // A bet is blocked if it triggers AT LEAST ONE rule — not necessarily all.
    // carol triggers only rule 2 (self-excluded), dave triggers only rule 1 (non-GB).
    // Both must appear in blocked output; eve triggers neither and must not appear.
    @Test
    void blocked_bets_includes_bets_triggering_any_rule() {
        Bet carol = bet("B003", "carol", 20.00, "GB", true);  // self-excluded
        Bet dave  = bet("B004", "dave",  20.00, "US", false); // non-GB region
        Bet eve   = bet("B005", "eve",   20.00, "GB", false); // passes both — not blocked

        Predicate<Bet> nonGbRegion  = b -> !b.region().equals("GB");
        Predicate<Bet> selfExcluded = Bet::selfExcluded;

        List<Bet> result = Solution.blockedBets(
                List.of(carol, dave, eve),
                List.of(nonGbRegion, selfExcluded)
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(b -> b.betRef().equals("B003")));
        assertTrue(result.stream().anyMatch(b -> b.betRef().equals("B004")));
    }

    // Empty rules → identity predicate (always false) → no bets blocked.
    // Using `bet -> true` as the identity here would block everything — wrong.
    @Test
    void blocked_bets_returns_empty_when_no_rules() {
        List<Bet> bets = List.of(
                bet("B001", "alice", 50.00, "GB", false),
                bet("B002", "bob",   50.00, "GB", false)
        );

        List<Bet> result = Solution.blockedBets(bets, List.of());

        assertTrue(result.isEmpty());
    }
}
