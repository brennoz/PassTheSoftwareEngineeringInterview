package com.interview.algorithms.java_features.q055_top_spender_per_department.solution;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Same problem as Solution.java, solved with plain loops and mutable accumulators — no
// Stream/Collectors API at all. Kept as a separate file specifically so the two approaches
// can be read side by side, not because this is the recommended way to solve it.
//
// Trade-offs vs the functional version (Solution.java):
//  - MORE explicit intermediate state: a nested Map<String, Map<String, BigDecimal>> has to
//    exist as a named variable here. In the functional version, the equivalent "per-employee
//    totals within a department" data only exists transiently inside the inner groupingBy
//    collector — nothing named holds it, because nothing outside that one collector step
//    needs to see it.
//  - A real bug class the functional version can't have: the max-tracking loop needs
//    sentinel variables (topEmployee/topAmount, both starting null) and a manual
//    null-or-greater-than check. Forgetting to initialise a sentinel correctly, or getting the
//    comparison direction backwards, is a mistake you can make here that simply has no
//    equivalent surface in Map.Entry.comparingByValue().max(...) — the library method can't
//    be "initialised wrong."
//  - MORE approachable to a reader who doesn't know the Collectors API — every line here is
//    ordinary control flow (for, if, Map.get/put) that reads the same in any language with
//    hash maps, no Stream-specific vocabulary required.
//  - Easier, arguably, to add a debug print or breakpoint mid-computation, since there's a
//    real loop body to pause inside — a stream pipeline's intermediate state is harder to
//    inspect mid-flight without restructuring the code (e.g. temporarily inserting a peek()).
public class ImperativeApproach {

    public static Map<String, String> topSpenderPerDepartment(List<Purchase> purchases) {
        // department -> (employeeName -> running total for that employee in that department)
        Map<String, Map<String, BigDecimal>> totalsByDeptAndEmployee = new HashMap<>();

        for (Purchase purchase : purchases) {
            Map<String, BigDecimal> employeeTotals =
                    totalsByDeptAndEmployee.computeIfAbsent(purchase.department(), d -> new HashMap<>());
            BigDecimal currentTotal = employeeTotals.getOrDefault(purchase.employeeName(), BigDecimal.ZERO);
            employeeTotals.put(purchase.employeeName(), currentTotal.add(purchase.amount()));
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Map<String, BigDecimal>> departmentEntry : totalsByDeptAndEmployee.entrySet()) {
            String topEmployee = null;
            BigDecimal topAmount = null;

            for (Map.Entry<String, BigDecimal> employeeEntry : departmentEntry.getValue().entrySet()) {
                if (topAmount == null || employeeEntry.getValue().compareTo(topAmount) > 0) {
                    topAmount = employeeEntry.getValue();
                    topEmployee = employeeEntry.getKey();
                }
            }

            result.put(departmentEntry.getKey(), topEmployee);
        }

        return result;
    }
}
