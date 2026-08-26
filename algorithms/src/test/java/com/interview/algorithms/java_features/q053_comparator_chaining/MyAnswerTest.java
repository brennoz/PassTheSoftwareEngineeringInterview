package com.interview.algorithms.java_features.q053_comparator_chaining;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void should_return_an_empoyee_list_ordered_by_department_and_salary_descending() {

        Employee alice = new Employee("e1", "MATH", 55000);
        Employee oscar = new Employee("e2", "MATH", 70000);
        Employee valentina = new Employee("e3", "BIOLOGY", 70000);

        List<Employee> unorderList = List.of(alice, oscar, valentina);
        
        List<Employee> orderedList = MyAnswer.sortByDepartmentThenSalaryDesc(unorderList);

        assertEquals(valentina, orderedList.get(0));
        assertEquals(oscar, orderedList.get(1));
        assertEquals(alice, orderedList.get(2));
    }

        @Test
    void doesNotMutateOrRequireMutableInput() {
        List<Employee> employees = List.of(
                new Employee("Carol", "Sales", 70000),
                new Employee("Alice", "Engineering", 90000)
        );

        List<Employee> sorted = MyAnswer.sortByDepartmentThenSalaryDesc(employees);

        // original order untouched
        assertEquals("Carol", employees.get(0).name());
        assertEquals("Alice", employees.get(1).name());
        // result is correctly sorted
        assertEquals("Alice", sorted.get(0).name());
        assertEquals("Carol", sorted.get(1).name());
    }
}
