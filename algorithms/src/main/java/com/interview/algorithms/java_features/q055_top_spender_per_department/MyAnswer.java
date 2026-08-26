package com.interview.algorithms.java_features.q055_top_spender_per_department;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyAnswer {
    // your implementation here

    public static Map<String, String> topSpenderPerDepartment(List<Purchase> purchases) {
        return purchases.stream()
                        .collect(Collectors.groupingBy(Purchase::department, 
                                            Collectors.collectingAndThen(
                                                Collectors.groupingBy(Purchase::employeeName, Collectors.reducing(BigDecimal.ZERO, Purchase::amount, BigDecimal::add)), 
                                                employeeTotals -> employeeTotals.entrySet().stream()
                                                                                            .max(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                                                                                            .map(Map.Entry::getKey)
                                                                                            .orElseThrow()
                                            )
                        )
                    );
                }      
}
