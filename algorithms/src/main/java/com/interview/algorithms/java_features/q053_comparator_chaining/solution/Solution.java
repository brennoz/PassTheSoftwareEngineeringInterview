package com.interview.algorithms.java_features.q053_comparator_chaining.solution;

import java.util.Comparator;
import java.util.List;

public class Solution {

    // The subtlety: .reversed() is called on the SALARY comparator alone, before it's handed
    // to thenComparing — not on the combined department+salary chain afterward. Comparator
    // composition builds up a single compare() function progressively; reversed() flips
    // whatever comparator it's called on at that point, so calling it on the whole chain
    // would flip department too. Calling it on just Comparator.comparing(Employee::salary)
    // flips ONLY that piece before it's folded into the chain.
    //
    // Equivalent alternative worth knowing: thenComparing(Employee::salary,
    // Comparator.reverseOrder()) — the 2-arg overload (keyExtractor, keyComparator) achieves
    // the same result without constructing an intermediate Comparator<Employee> object first.
    //
    // .stream().sorted(...).toList() rather than employees.sort(comparator): sort() mutates
    // its receiver in place, which fails on an immutable List (e.g. List.of(...)) and mutates
    // the caller's list otherwise — either way, not what "return a new sorted list, don't
    // mutate the input" asked for.
    public static List<Employee> sortByDepartmentThenSalaryDesc(List<Employee> employees) {
        Comparator<Employee> byDepartmentThenSalaryDesc = Comparator
                .comparing(Employee::department)
                .thenComparing(Comparator.comparing(Employee::salary).reversed());

        return employees.stream()
                .sorted(byDepartmentThenSalaryDesc)
                .toList();
    }
}
