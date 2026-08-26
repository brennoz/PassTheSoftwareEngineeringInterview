package com.interview.algorithms.java_features.q052_grouping_collectors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void sumsAmountsPerStatusAcrossMultipleGroups() {
        List<Order> orders = List.of(
                new Order("o1", "PAID", new BigDecimal("10.00")),
                new Order("o2", "PAID", new BigDecimal("5.50")),
                new Order("o3", "PENDING", new BigDecimal("20.00"))
        );

        Map<String, BigDecimal> result = MyAnswer.totalAmountByStatus(orders);

        assertEquals(0, result.get("PAID").compareTo(new BigDecimal("15.50")));
        assertEquals(0, result.get("PENDING").compareTo(new BigDecimal("20.00")));
    }

    @Test
    void statusWithSingleOrderReturnsThatAmount() {
        List<Order> orders = List.of(new Order("o1", "REFUNDED", new BigDecimal("7.25")));

        Map<String, BigDecimal> result = MyAnswer.totalAmountByStatus(orders);

        assertEquals(0, result.get("REFUNDED").compareTo(new BigDecimal("7.25")));
    }

    @Test
    void emptyListReturnsEmptyMap() {
        Map<String, BigDecimal> result = MyAnswer.totalAmountByStatus(List.of());

        assertTrue(result.isEmpty());
    }
}
