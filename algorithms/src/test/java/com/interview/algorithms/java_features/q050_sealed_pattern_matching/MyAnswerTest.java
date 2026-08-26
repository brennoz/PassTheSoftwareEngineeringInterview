package com.interview.algorithms.java_features.q050_sealed_pattern_matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {
    private static final double DELTA = 1e-9;

    @Test
    void circleAreaUsesPiRadiusSquared() {
        assertEquals(Math.PI * 4, MyAnswer.area(new Circle(2)), DELTA);
    }

    @Test
    void rectangleAreaIsWidthTimesHeight() {
        assertEquals(12.0, MyAnswer.area(new Rectangle(3, 4)), DELTA);
    }

    @Test
    void triangleAreaIsHalfBaseTimesHeight() {
        assertEquals(12.0, MyAnswer.area(new Triangle(6, 4)), DELTA);
    }
}
