package com.interview.algorithms.java_features.q050_sealed_pattern_matching.solution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    private static final double DELTA = 1e-9;

    @Test
    void circleAreaUsesPiRadiusSquared() {
        assertEquals(Math.PI * 4, Solution.area(new Circle(2)), DELTA);
    }

    @Test
    void rectangleAreaIsWidthTimesHeight() {
        assertEquals(12.0, Solution.area(new Rectangle(3, 4)), DELTA);
    }

    @Test
    void triangleAreaIsHalfBaseTimesHeight() {
        assertEquals(12.0, Solution.area(new Triangle(6, 4)), DELTA);
    }
}
