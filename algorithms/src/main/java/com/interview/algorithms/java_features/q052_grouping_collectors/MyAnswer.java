package com.interview.algorithms.java_features.q052_grouping_collectors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyAnswer {
    // your implementation here

    public static Map<String, BigDecimal> totalAmountByStatus(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(order -> order.status()
                        ,Collectors.reducing(BigDecimal.ZERO, order -> order.amount(), BigDecimal::add)));
    }
}
