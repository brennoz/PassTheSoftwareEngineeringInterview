package com.interview.algorithms.java_features.q052_grouping_collectors.solution;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("o1", "PAID", new BigDecimal("10.00")),
                new Order("o2", "PAID", new BigDecimal("5.50")),
                new Order("o3", "PENDING", new BigDecimal("20.00")),
                new Order("o4", "REFUNDED", new BigDecimal("7.25"))
        );

        Map<String, BigDecimal> totals = Solution.totalAmountByStatus(orders);
        totals.forEach((status, total) -> System.out.println(status + ": " + total));
        // PAID: 15.50
        // PENDING: 20.00
        // REFUNDED: 7.25
    }
}
