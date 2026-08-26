package com.interview.algorithms.java_features.q050_sealed_pattern_matching.solution;

public class Solution {

    // Pattern: exhaustive switch expression over a sealed type, with record deconstruction
    // patterns (case Circle(double radius) -> ...) — Java 21. No default branch: Shape's
    // `permits` list has exactly 3 members, so the compiler proves every case is covered and
    // REFUSES to compile if a branch is missing. That's the actual payoff over instanceof +
    // if/else ending in a default that throws at runtime: a missing case here is a compile
    // error, not a bug someone finds in production the day a 4th shape is added.
    //
    // What NOT to do: don't add `default -> throw new IllegalStateException(...)` "just in
    // case" — with a sealed interface that default branch is provably unreachable dead code,
    // and worse, it silently defeats exhaustiveness checking: if a teammate later adds
    // `Square` to `permits`, this switch would still compile (falling into the now-wrong
    // default) instead of failing to compile at the exact line that needs updating.
    public static double area(Shape shape) {
        return switch (shape) {
            case Circle(double radius) -> Math.PI * radius * radius;
            case Rectangle(double width, double height) -> width * height;
            case Triangle(double base, double height) -> 0.5 * base * height;
        };
    }
}
