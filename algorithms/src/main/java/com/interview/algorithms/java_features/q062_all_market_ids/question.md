# Exercise — All Market IDs, Sorted and Distinct (Medium)

**Concepts:** `Stream.flatMap`, `Stream.distinct`, `Stream.sorted`
**Estimated time:** 20 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java`

---

## Problem

Betting domain: a `BetSlip` holds a `List<Selection>`, and each `Selection` has a `marketId`.
Implement, in `MyAnswer.java`:

```java
public static List<String> allMarketIdsSortedDistinct(List<BetSlip> slips)
```

Given `List<BetSlip>`, return every distinct `marketId` referenced by any selection on any
slip, sorted alphabetically. The same `marketId` can appear more than once — within one slip,
or across different slips — and must only appear once in the result.

## Hard constraints (the actual point of the exercise)

- Use `Stream.flatMap` to go from `List<BetSlip>` to a flat stream of `Selection` — not a
  `.map(BetSlip::selections)` left un-flattened, and not a manual nested loop.
- Use `distinct()` and `sorted()` — not a `Set` you manually convert and sort yourself.

## Key questions before coding

- If you `.map(BetSlip::selections)` on a `Stream<BetSlip>`, what's the resulting stream's
  element type? Why can't you call `.map(Selection::marketId)` directly on that without an
  extra step — and what does `flatMap` do differently that avoids needing one?
- Does it matter whether you call `.distinct()` before or after `.sorted()` for correctness?
  What about for the number of comparisons `sorted()` ends up doing?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: selections in different slips getting flattened
   into one stream; the same `marketId` appearing in two different slips collapsing to one
   entry in the result; a slip with an empty selection list contributing nothing.
2. Red → fail. Implement `allMarketIdsSortedDistinct()`. Green → pass.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — a nested loop populating a `TreeSet` (sorted +
deduplicated by construction) instead of `flatMap`/`distinct`/`sorted`.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q062_all_market_ids.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
