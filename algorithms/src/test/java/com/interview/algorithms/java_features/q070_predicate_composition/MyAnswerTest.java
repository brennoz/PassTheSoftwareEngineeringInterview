package com.interview.algorithms.java_features.q070_predicate_composition;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

// Write your tests here — TDD: red first, then implement in MyAnswer.java.
class MyAnswerTest {

    // --- helpers ---

    private static MyAnswer.Bet bet(String ref, String bettor, double stake, String region, boolean selfExcluded) {
        return new MyAnswer.Bet(ref, bettor, BigDecimal.valueOf(stake), region, selfExcluded);
    }

    // TODO: write tests for approvedBets and blockedBets
    // Suggested coverage:
    //   1. approvedBets — multiple rules, verify bets failing any rule are excluded
    //   2. approvedBets — empty rules list → all bets returned
    //   3. blockedBets — multiple rules, verify bets triggering at least one rule are included
    //   4. blockedBets — empty rules list → empty list returned
}
