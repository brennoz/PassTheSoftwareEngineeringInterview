# Exercise — Sealed Interfaces + Pattern Matching for Switch (Easy)

**Java 21 features:** sealed interfaces, record patterns, exhaustive `switch` expressions
**Estimated time:** 15 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting)

---

## Problem

Given:

```java
public sealed interface Shape permits Circle, Rectangle, Triangle {}
public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
public record Triangle(double base, double height) implements Shape {}
```

Implement, in `MyAnswer.java`:

```java
public static double area(Shape shape)
```

- `Circle` → `π × radius²`
- `Rectangle` → `width × height`
- `Triangle` → `0.5 × base × height`

## Hard constraints (this is the actual point of the exercise)

- **No `instanceof`.**
- **No `if`/`else if` chains.**
- Implement it as a single `switch` **expression** using pattern matching (`case Circle c ->
  ...`), ideally with **record deconstruction patterns** (`case Circle(double radius) -> ...`)
  so you can use `radius` directly without calling `.radius()`.
- **Do not add a `default` branch.** `Shape` is `sealed` with exactly 3 permitted
  implementations — the compiler can prove the switch is exhaustive without one. Adding a
  `default` anyway defeats the actual point of sealing the interface (see Key Questions).

## Key questions before coding

- What compiler error do you get if you delete the `case Triangle` branch entirely? What does
  that error tell you about what sealed interfaces + exhaustive switch actually buy you over a
  regular (non-sealed) interface with an `instanceof` chain ending in a `default ->` that
  throws?
- If a teammate adds a fourth shape, `record Square(double side) implements Shape`, to the
  `permits` list next month, what happens to THIS switch — does it compile, and if not, where
  does the compiler point them?

## TDD workflow

1. Write `MyAnswerTest.java` in `src/test/java/.../q050_sealed_pattern_matching/` yourself —
   no test file is provided. Cover all three shapes; use a small delta (`1e-9`) for the circle
   assertion since it involves `Math.PI`.
2. Red → fail. Implement `area()`. Green → pass.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q050_sealed_pattern_matching.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
