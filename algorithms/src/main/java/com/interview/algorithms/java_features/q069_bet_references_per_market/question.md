# Exercise — Bet References Per Market (Medium)

**Lessons:** [0010 Collectors.mapping](../../../../../topics/concepts/java/0010-collectors-mapping.html)
**Concepts:** `Collectors.mapping` as a `groupingBy` downstream · `collectingAndThen` for post-processing · `Map.computeIfAbsent` (imperative)
**Estimated time:** 20 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see `solution/ImperativeApproach.java`

---

## Domain

Sport betting back-office. A market is a thing you can bet on (e.g. "Match Winner — Arsenal vs Chelsea"). Each `Bet` carries a unique reference, the market it belongs to, the bettor's name, and the stake.

```java
public record Bet(String betRef, String marketId, String bettor, BigDecimal stake) {}
```

---

## Problem

Implement, in `MyAnswer.java`:

```java
public static Map<String, List<String>> betRefsByMarket(List<Bet> bets)
```

Given a list of bets, return a map from **marketId** → **sorted list of bet references** placed in that market. Sorted means ascending lexicographic order of `betRef`.

---

## Hard constraints (the actual point of the exercise)

- Use **`Collectors.mapping`** as the downstream collector inside `groupingBy` — do not call `groupingBy(key)` and then start a second stream over the resulting map.
- The sort must be **per-group** (within each market's list), not a global pre-sort on the stream.
- Empty input → empty map (not null, not an exception).

---

## Key questions before coding

- `groupingBy(Bet::marketId)` gives `Map<String, List<Bet>>`. What do you need to change so the value type is `List<String>` (betRef) instead?
- If you add `collectingAndThen(mapping(..., toList()), finisher)` as the downstream, when does `finisher` run relative to accumulation — before the first element, after each element, or after the whole group is complete?
- What does the imperative version look like? What map method creates the accumulator list only on first encounter of a key?

---

## TDD workflow

1. Write `MyAnswerTest.java` yourself in the test directory. Cover:
   - Multiple bets across multiple markets — verify refs grouped and sorted per market.
   - A market with a single bet — single-element list.
   - Empty input → empty map.
2. Red → fail. Implement. Green → pass.

---

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — it solves the same problem with `computeIfAbsent` and shows concretely why the two-pass approach wastes an allocation per repeated key.

---

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q069_bet_references_per_market.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
