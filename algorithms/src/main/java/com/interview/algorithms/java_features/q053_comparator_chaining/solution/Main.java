package com.interview.algorithms.java_features.q053_comparator_chaining.solution;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("Alice", "Engineering", 90000),
                new Employee("Bob", "Engineering", 110000),
                new Employee("Carol", "Sales", 70000),
                new Employee("Dave", "Sales", 95000)
        );

        Solution.sortByDepartmentThenSalaryDesc(employees)
                .forEach(e -> System.out.println(e.department() + " | " + e.salary() + " | " + e.name()));
        // Engineering | 110000 | Bob
        // Engineering | 90000  | Alice
        // Sales       | 95000  | Dave
        // Sales       | 70000  | Carol
    }
}
