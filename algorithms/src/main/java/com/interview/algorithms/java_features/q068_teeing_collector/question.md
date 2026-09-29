# Exercise — Bet Settlement Report (Medium)

**Concepts:** `Collectors.teeing` (Java 12), `Collectors.filtering` (Java 9), `Collectors.reducing` (3-arg identity form)
**Estimated time:** 20 min
**Lessons:** [0009 Collectors.teeing](../../../../../topics/concepts/java/0009-teeing-collector.html)
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see `solution/ImperativeApproach.java`

---

## Domain context

After an event settles, the risk desk needs a `SettlementReport` with two figures:
- **Total payout to winners** — sum of `payout` across won bets only
- **Total settled count** — how many bets were processed (won + lost)

These two aggregations have **different** inclusion criteria: payout sums only won bets,
count includes everyone. You need both in **a single stream pass**.

## Records (already defined — do not re-declare)

```java
public record Settlement(String betId, BigDecimal payout, boolean won) {}
public record SettlementReport(BigDecimal totalWinnerPayout, long settledCount) {}
```

## Problem

Implement, in `MyAnswer.java`:

```java
public static SettlementReport buildReport(List<Settlement> settlements)
```

Given a list of `Settlement` records, return a `SettlementReport` where:
- `totalWinnerPayout` = sum of `payout` for **won** settlements only
- `settledCount` = count of **all** settlements (won + lost)

## Hard constraints (the actual point of the exercise)

- Use `Collectors.teeing` — a single stream pass, no intermediate collection.
- `totalWinnerPayout` must be `BigDecimal.ZERO` (not `null`, not `Optional`) when no settlement was won.
- No `double`/`float` arithmetic.

## Key questions before coding

- Why can't you pre-filter with `.filter(Settlement::won)` before `.collect(teeing(...))`?  
  *(What does that do to `settledCount`?)*
- `teeing(collector1, collector2, merger)` — what type is `merger`?
- `Collectors.filtering(predicate, downstream)` vs `.filter(predicate)` on the stream:  
  when do you need the Collector version rather than the stream operation?
- For `totalWinnerPayout`: you need `BigDecimal` directly, not `Optional<BigDecimal>`.  
  Which overload of `Collectors.reducing` returns `T` directly instead of `Optional<T>`?

## TDD workflow

1. Write `MyAnswerTest.java`. Cover:
   - Mixed list (winners + losers) — verify `totalWinnerPayout` AND `settledCount` independently
   - All-lost list — `totalWinnerPayout` → `BigDecimal.ZERO`, `settledCount` → correct total  
     *(this is the pre-filter trap test — if you pre-filtered, settledCount would be 0 here)*
   - Empty list — both fields → zero defaults
2. Red → fail. Implement `buildReport`. Green → pass.
3. Use `assertEquals(0, actual.compareTo(expected))` for `BigDecimal` comparisons.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — two accumulators, one for-each loop. Notice what the
functional version gives you structurally that the imperative version cannot (composability as a
downstream Collector, parallel-stream-readiness without code changes).

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q068_teeing_collector.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
