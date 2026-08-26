package com.interview.algorithms.java_features.q053_comparator_chaining;

import java.util.Comparator;
import java.util.List;

public class MyAnswer {
    // your implementation here

    public static List<Employee> sortByDepartmentThenSalaryDesc(List<Employee> employees) {

        return employees.stream()
                .sorted(Comparator.comparing(Employee::department)
                                  .thenComparing(Comparator.comparing(Employee::salary).reversed()))
                .toList();
            
    }
}
