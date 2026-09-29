# Exercise — Partitioned Payment Totals (Medium)

**Concepts:** `Collectors.partitioningBy` (2-arg with downstream), `Collectors.reducing`
(3-arg, identity form), `partitioningBy` vs `groupingBy` guarantee on both keys
**Estimated time:** 20 min
**Lessons:** [0003 Streams](../../../../../topics/concepts/java/0003-streams.html)
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java`

---

## Domain context

Payment reconciliation: at end of day, the finance team needs two numbers — total value of
successful payments (for settlement) and total value of failed payments (for retry/reporting).

## Record (already defined — do not re-declare)

```java
public record Transaction(String id, BigDecimal amount, boolean successful) {}
```

## Problem

Implement, in `MyAnswer.java`:

```java
public static Map<Boolean, BigDecimal> totalByOutcome(List<Transaction> transactions)
```

Given a list of `Transaction` records, return a map where:
- `true`  → sum of `amount` for all **successful** transactions
- `false` → sum of `amount` for all **failed** transactions

Both keys must always be present in the result — even if one partition is empty (e.g. all
transactions succeeded, `false` → `BigDecimal.ZERO`).

## Hard constraints (the actual point of the exercise)

- Use `Collectors.partitioningBy` — not `groupingBy`.
- Use `Collectors.reducing` as the downstream collector — `BigDecimal` has no dedicated
  `summingBigDecimal`, and `summingDouble` violates the project's monetary-precision rule.
- The 3-arg `reducing(identity, mapper, combiner)` overload returns `T` directly.
  The 1-arg overload (BinaryOperator only) returns `Optional<T>`. Pick accordingly.
- No `double`/`float` arithmetic anywhere.

## Key questions before coding

- `partitioningBy` vs `groupingBy(t -> t.successful())`: both group by a boolean. What's the
  concrete difference in the returned map when all transactions are successful?
- `Collectors.reducing(BigDecimal.ZERO, Transaction::amount, BigDecimal::add)` vs
  `Collectors.reducing(BigDecimal::add)`: what is the return type of each, and which fits
  `Map<Boolean, BigDecimal>` directly?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: mixed list (both partitions non-empty); all
   successful (false key → BigDecimal.ZERO); empty list (both keys → BigDecimal.ZERO).
2. Red → fail. Implement `totalByOutcome`. Green → pass.
3. Use `assertEquals(0, actual.compareTo(expected))` for BigDecimal comparisons.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — two `BigDecimal` accumulators, one loop.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q067_partitioned_payment_totals.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
