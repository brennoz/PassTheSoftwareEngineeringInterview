package com.interview.algorithms.java_features.q052_grouping_collectors.solution;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

    // 3-arg groupingBy: classifier (Order::status) partitions the stream into groups, then
    // the downstream collector runs over each group's List<Order> independently — that's
    // what turns Map<String, List<Order>> into Map<String, BigDecimal> in one pass, instead
    // of grouping first and then mapping the values in a second step.
    //
    // Collectors.reducing(BigDecimal.ZERO, Order::amount, BigDecimal::add): the general-form
    // reduction collector. identity = BigDecimal.ZERO (the sum of nothing), mapper =
    // Order::amount (what to extract from each element before combining), op = BigDecimal::add
    // (how to combine two values). This is exactly the shape summingInt/summingDouble use
    // internally for int/double — reducing() is the one that isn't type-restricted, so it's
    // the one available for BigDecimal (or any other type Collectors didn't special-case).
    public static Map<String, BigDecimal> totalAmountByStatus(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        Order::status,
                        Collectors.reducing(BigDecimal.ZERO, Order::amount, BigDecimal::add)));
    }
}
