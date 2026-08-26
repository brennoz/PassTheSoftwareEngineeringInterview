package com.interview.algorithms.java_features.q055_top_spender_per_department.solution;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    // Three collectors composed into one pipeline, from the inside out:
    //  1. groupingBy(Purchase::employeeName, reducing(...))  — per-employee total, WITHIN
    //     whatever department the outer groupingBy has already partitioned by. This is the
    //     downstream collector passed to the OUTER groupingBy, so it runs once per department,
    //     only over that department's purchases.
    //  2. collectingAndThen(innerGroupingBy, finisher) — takes the Map<employee, total> that
    //     step 1 produces for a department, and immediately transforms it into a single
    //     employee name (the finisher) before it's stored as this department's value in the
    //     outer map. Without collectingAndThen, the outer map's values would stay
    //     Map<String, BigDecimal> — collectingAndThen is what turns "grouped data" into "the
    //     answer derived from that grouped data," in the same collect() call.
    //  3. groupingBy(Purchase::department, <steps 1+2>) — the outer partition.
    //
    // Comparing imperative vs functional here (see ImperativeApproach.java for the full loop
    // version): the functional pipeline reads as a description of the RESULT ("group by
    // department, and within each group, group by employee-total and take the max") — no
    // mutable accumulator variables exist anywhere in this method. The imperative version
    // needs an explicit Map<String, Map<String, BigDecimal>> to hold intermediate per-employee
    // totals, plus a manual max-tracking loop with sentinel null checks (topEmployee/topAmount
    // starting null, updated conditionally). Neither is "wrong" — the functional version is
    // shorter and has no mutable state to accidentally get wrong (e.g. forgetting to reset a
    // sentinel between department iterations, a real bug class in the imperative shape), but
    // it front-loads three Collectors API calls a reader needs to already know; the imperative
    // version is longer but every line is plain control flow anyone can read regardless of
    // Stream API familiarity — a real trade-off, not a strictly-better-or-worse situation.
    public static Map<String, String> topSpenderPerDepartment(List<Purchase> purchases) {
        return purchases.stream()
                .collect(Collectors.groupingBy(
                        Purchase::department,
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(
                                        Purchase::employeeName,
                                        Collectors.reducing(BigDecimal.ZERO, Purchase::amount, BigDecimal::add)),
                                employeeTotals -> employeeTotals.entrySet().stream()
                                        .max(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                                        .map(Map.Entry::getKey)
                                        .orElseThrow()
                        )
                ));
    }
}
