package com.interview.algorithms.java_features.q050_sealed_pattern_matching.solution;

public class Main {

    public static void main(String[] args) {
        System.out.println(Solution.area(new Circle(2)));        // ~12.566
        System.out.println(Solution.area(new Rectangle(3, 4)));  // 12.0
        System.out.println(Solution.area(new Triangle(6, 4)));   // 12.0
    }
}
