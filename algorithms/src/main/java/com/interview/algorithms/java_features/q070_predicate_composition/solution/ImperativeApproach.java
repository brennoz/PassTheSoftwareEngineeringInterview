package com.interview.algorithms.java_features.q070_predicate_composition.solution;

import com.interview.algorithms.java_features.q070_predicate_composition.MyAnswer.Bet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// Imperative equivalents — shows exactly what stream().reduce(identity, Predicate::and) replaces.
//
// Key tradeoffs vs the functional version:
//   - early break on first failure is explicit here; Predicate.and() short-circuits automatically
//     because Java evaluates the left side first and skips the right when false
//   - the combined predicate is not a reusable first-class value — you can't store it, log it,
//     or pass it to another method without wrapping the loop again
//   - adding a "blocked AND approved" cross-check would require duplicating the loop body;
//     the functional version just assigns two different composed predicates
public class ImperativeApproach {

    public static List<Bet> approvedBets(List<Bet> bets, List<Predicate<Bet>> rules) {
        List<Bet> result = new ArrayList<>();
        outer:
        for (Bet bet : bets) {
            for (Predicate<Bet> rule : rules) {
                if (!rule.test(bet)) {
                    continue outer; // fails a rule — skip this bet
                }
            }
            result.add(bet);
        }
        return result;
    }

    public static List<Bet> blockedBets(List<Bet> bets, List<Predicate<Bet>> rules) {
        List<Bet> result = new ArrayList<>();
        for (Bet bet : bets) {
            for (Predicate<Bet> rule : rules) {
                if (rule.test(bet)) {
                    result.add(bet);
                    break; // one rule fired — no need to test the rest
                }
            }
        }
        return result;
    }
}
