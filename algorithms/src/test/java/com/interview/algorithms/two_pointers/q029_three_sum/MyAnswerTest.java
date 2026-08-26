package com.interview.algorithms.two_pointers.q029_three_sum;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyAnswerTest {

    private final MyAnswer solution = new MyAnswer();

    @Test
    void basic_case_returns_two_unique_triplets() {
        var result = solution.threeSum(new int[]{-1, 0, 1, 2, -1, -4});
        assertEquals(2, result.size());
        assertTrue(result.contains(List.of(-1, -1, 2)));
        assertTrue(result.contains(List.of(-1, 0, 1)));
    }

    @Test
    void all_zeros_returns_one_triplet() {
        var result = solution.threeSum(new int[]{0, 0, 0});
        assertEquals(1, result.size());
        assertEquals(List.of(0, 0, 0), result.get(0));
    }

    @Test
    void no_valid_triplet_returns_empty() {
        var result = solution.threeSum(new int[]{1, 2, 3});
        assertTrue(result.isEmpty());
    }
}
