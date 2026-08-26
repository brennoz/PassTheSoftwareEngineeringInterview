# Exercise — Grouping + Downstream Collectors (Medium)

**Concepts:** `Collectors.groupingBy` (2-arg vs 3-arg form), downstream collector composition,
`Collectors.reducing`
**Estimated time:** 25 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting)

---

## Problem

Given `Order(String id, String status, BigDecimal amount)`, implement, in `MyAnswer.java`:

```java
public static Map<String, BigDecimal> totalAmountByStatus(List<Order> orders)
```

Return a map from status → the sum of `amount` across every order with that status. Empty
input → empty map. This must be **one stream pipeline** — no manual `for` loop building up a
mutable `Map` by hand.

## The actual trap this exercise is testing for

`Collectors` ships `summingInt`, `summingLong`, and `summingDouble` — but **there is no
`summingBigDecimal`**. This project's standing rule is `BigDecimal` for money, never `double`
(precision), so reaching for `summingDouble` here isn't an option even though it's the first
thing autocomplete offers you. You need a downstream collector that can sum an arbitrary
type, not just the three the library special-cased.

## Key questions before coding

- `Collectors.groupingBy` has a 2-argument form (classifier only — you get
  `Map<K, List<T>>`) and a 3-argument form (classifier + a *downstream* collector, applied to
  each group's elements). Which one do you need here, and why can't the 2-arg form alone get
  you to `Map<String, BigDecimal>`?
- `Collectors.reducing(identity, mapper, op)` is the general-purpose "combine everything into
  one value" collector — it's what backs `summingInt`/`summingDouble` internally for the types
  the library DID special-case. What would `identity`, `mapper`, and `op` need to be to sum
  `BigDecimal` amounts with it?

## TDD workflow

1. Write `MyAnswerTest.java` yourself in `src/test/java/.../q052_grouping_collectors/`.
   Cover: multiple orders across multiple statuses summing correctly, a status with a single
   order, and the empty-list case. Compare `BigDecimal` results with
   `assertEquals(0, actual.compareTo(expected))`, never a direct `assertEquals` on two
   `BigDecimal`s (scale-sensitive — this project's standing rule).
2. Red → fail. Implement `totalAmountByStatus()`. Green → pass.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q052_grouping_collectors.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
