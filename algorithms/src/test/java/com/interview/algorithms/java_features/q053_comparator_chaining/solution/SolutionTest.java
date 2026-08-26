package com.interview.algorithms.java_features.q053_comparator_chaining.solution;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    // What this verifies: department stays ASCENDING while salary goes descending — a
    // solution that accidentally calls .reversed() on the whole chain (instead of just the
    // salary comparator) would sort departments Z->A too. "Engineering" before "Sales" here
    // only holds if department ordering wasn't flipped.
    @Test
    void sortsByDepartmentAscendingThenSalaryDescending() {
        List<Employee> employees = List.of(
                new Employee("Alice", "Engineering", 90000),
                new Employee("Bob", "Engineering", 110000),
                new Employee("Carol", "Sales", 70000),
                new Employee("Dave", "Sales", 95000)
        );

        List<Employee> sorted = Solution.sortByDepartmentThenSalaryDesc(employees);

        List<String> namesInOrder = sorted.stream().map(Employee::name).toList();
        assertEquals(List.of("Bob", "Alice", "Dave", "Carol"), namesInOrder);
    }

    // What this verifies: the method doesn't mutate its input and doesn't assume the input
    // is a mutable list — a solution using employees.sort(comparator) directly would throw
    // UnsupportedOperationException here, since List.of(...) is immutable.
    @Test
    void doesNotMutateOrRequireMutableInput() {
        List<Employee> employees = List.of(
                new Employee("Carol", "Sales", 70000),
                new Employee("Alice", "Engineering", 90000)
        );

        List<Employee> sorted = Solution.sortByDepartmentThenSalaryDesc(employees);

        // original order untouched
        assertEquals("Carol", employees.get(0).name());
        assertEquals("Alice", employees.get(1).name());
        // result is correctly sorted
        assertEquals("Alice", sorted.get(0).name());
        assertEquals("Carol", sorted.get(1).name());
    }
}
