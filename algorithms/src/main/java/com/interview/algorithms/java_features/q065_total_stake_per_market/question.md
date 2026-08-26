# Exercise — Total Stake Per Market, Sorted (Medium)

**Concepts:** `Collectors.groupingBy` (3-arg overload with a map-factory), `Collectors.reducing`,
`TreeMap`
**Estimated time:** 20 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java`

---

## Problem

Betting domain: total liability exposure per market. Implement, in `MyAnswer.java`:

```java
public static Map<String, BigDecimal> totalStakePerMarketSorted(List<Bet> bets)
```

Given `List<Bet>` (`id`, `marketId`, `BigDecimal stake`), return a `Map<String, BigDecimal>` of
total stake per `marketId`. The returned map's iteration order must be sorted by `marketId`,
regardless of the order markets appear in the input.

## Hard constraints (the actual point of the exercise)

- Use `Collectors.groupingBy`'s 3-argument overload — `(classifier, mapFactory, downstream)` —
  supplying `TreeMap::new` as the map factory. Not the 2-arg or classifier+downstream 3-arg
  overloads (both back the result with a `HashMap`, whose iteration order is unspecified), and
  not sorting entries into a `List` afterward.
- Use `Collectors.reducing` for the per-market sum — `BigDecimal` has no dedicated
  `summingBigDecimal` collector, and `summingDouble` isn't an option under this project's
  BigDecimal-for-money rule.

## Key questions before coding

- `groupingBy` has three overloads. What does the middle argument in the 3-arg
  `(classifier, mapFactory, downstream)` form actually control, and how is that different from
  the 3-arg `(classifier, downstream)` overload that doesn't take a map factory at all?
- Why is a `HashMap`'s iteration order "unspecified" rather than just "happens to look
  consistent" — is it safe to rely on it looking sorted just because it did in a quick manual
  test?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: stakes on the same market summing correctly
   across multiple bets; the result map's key order being sorted even when the input list's
   marketIds are not.
2. Red → fail. Implement `totalStakePerMarketSorted()`. Green → pass.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — a `TreeMap` populated via `Map.merge()` in a loop
instead of `groupingBy` + `reducing`.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q065_total_stake_per_market.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
