package com.interview.algorithms.java_features.q055_top_spender_per_department.solution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    // What this verifies: totals are summed PER EMPLOYEE before comparing, not compared
    // purchase-by-purchase. Bob's two purchases (150 + 200 = 350) must out-total Alice's
    // single larger-looking purchase (300) — a solution that compared individual Purchase
    // amounts instead of per-employee sums would wrongly pick Alice.
    @Test
    void picksEmployeeWithHighestSummedTotalNotLargestSinglePurchase() {
        List<Purchase> purchases = List.of(
                new Purchase("Alice", "Engineering", new BigDecimal("300.00")),
                new Purchase("Bob", "Engineering", new BigDecimal("150.00")),
                new Purchase("Bob", "Engineering", new BigDecimal("200.00"))
        );

        Map<String, String> result = Solution.topSpenderPerDepartment(purchases);

        assertEquals("Bob", result.get("Engineering"));
    }

    @Test
    void computesIndependentlyPerDepartment() {
        List<Purchase> purchases = List.of(
                new Purchase("Alice", "Engineering", new BigDecimal("300.00")),
                new Purchase("Bob", "Engineering", new BigDecimal("150.00")),
                new Purchase("Bob", "Engineering", new BigDecimal("200.00")),
                new Purchase("Carol", "Sales", new BigDecimal("500.00")),
                new Purchase("Dave", "Sales", new BigDecimal("100.00"))
        );

        Map<String, String> result = Solution.topSpenderPerDepartment(purchases);

        assertEquals("Bob", result.get("Engineering"));
        assertEquals("Carol", result.get("Sales"));
    }

    @Test
    void emptyListReturnsEmptyMap() {
        Map<String, String> result = Solution.topSpenderPerDepartment(List.of());

        assertTrue(result.isEmpty());
    }

    // What this verifies: the functional pipeline and the hand-written loop version agree on
    // a case involving multiple departments and a multi-purchase employee — not just that
    // both happen to pass their own separate tests, but that they produce IDENTICAL output
    // for the same input, which is the actual claim the question.md comparison section makes.
    @Test
    void functionalAndImperativeApproachesAgree() {
        List<Purchase> purchases = List.of(
                new Purchase("Alice", "Engineering", new BigDecimal("300.00")),
                new Purchase("Bob", "Engineering", new BigDecimal("150.00")),
                new Purchase("Bob", "Engineering", new BigDecimal("200.00")),
                new Purchase("Carol", "Sales", new BigDecimal("500.00")),
                new Purchase("Dave", "Sales", new BigDecimal("100.00"))
        );

        assertEquals(
                Solution.topSpenderPerDepartment(purchases),
                ImperativeApproach.topSpenderPerDepartment(purchases)
        );
    }
}
