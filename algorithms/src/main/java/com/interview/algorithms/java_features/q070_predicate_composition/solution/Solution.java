package com.interview.algorithms.java_features.q070_predicate_composition.solution;

import com.interview.algorithms.java_features.q070_predicate_composition.MyAnswer.Bet;

import java.util.List;
import java.util.function.Predicate;

public class Solution {

    // Identity for .and() is "always true" — if the rules list is empty, every bet passes.
    // This matches boolean algebra: true AND anything = anything, so the identity element
    // for conjunction is true.
    //
    // Alternative: reduce(Predicate::and) returns Optional<Predicate<Bet>>, forcing a
    // .orElse(bet -> true) unwrap. The 2-arg reduce is cleaner because the identity handles
    // the empty-list case transparently without an Optional branch.
    //
    // What NOT to do: calling .filter(rule) in a loop changes the order-of-short-circuit and
    // makes it impossible to treat the combined predicate as a first-class value (e.g. pass it
    // to a scheduled audit report generator that expects a single Predicate<Bet>).
    public static List<Bet> approvedBets(List<Bet> bets, List<Predicate<Bet>> rules) {
        Predicate<Bet> combined = rules.stream()
                .reduce(bet -> true, Predicate::and);
        return bets.stream()
                .filter(combined)
                .toList();
    }

    // Identity for .or() is "always false" — if the rules list is empty, no bet is blocked.
    // Boolean algebra: false OR anything = anything, so false is the identity for disjunction.
    //
    // The trap: using `bet -> true` as the identity here makes every bet blocked regardless
    // of the actual rules — the opposite of the intended empty-list behaviour.
    //
    // Alternative rejected: negate approvedBets (inverted rule semantics). Works only if the
    // two rule lists share the same predicate polarity convention — which they deliberately
    // don't here (one list is "safe" predicates, the other is "risk" predicates), so they
    // must be composed independently.
    public static List<Bet> blockedBets(List<Bet> bets, List<Predicate<Bet>> rules) {
        Predicate<Bet> anyFlagged = rules.stream()
                .reduce(bet -> false, Predicate::or);
        return bets.stream()
                .filter(anyFlagged)
                .toList();
    }
}
