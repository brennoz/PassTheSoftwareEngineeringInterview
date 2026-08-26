package com.interview.algorithms.java_features.q050_sealed_pattern_matching;

public class MyAnswer {
    // your implementation here

    public static double area(Shape shape) {
        return switch(shape) {
            case Circle(double radius) -> Math.PI * (radius * radius);
            case Rectangle(double width, double height) -> width * height;
            case Triangle(double base, double height) -> (base * height)/2;
        };
    }
}
