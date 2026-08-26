# Exercise — Top Clean Transactions by Amount (Medium)

**Concepts:** `Collectors.partitioningBy`, `Comparator.comparing().reversed().thenComparing()`,
`Stream.sorted().limit()`
**Estimated time:** 25 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java`

---

## Problem

Fraud-review domain: a batch of payment transactions, some already flagged for manual review.
Implement, in `MyAnswer.java`:

```java
public static List<Transaction> topCleanTransactions(List<Transaction> transactions, int topN)
```

Given `List<Transaction>` (`id`, `BigDecimal amount`, `boolean flaggedForReview`), return the
top `topN` **unflagged** transactions ordered by `amount` descending. If two transactions tie
on amount, break the tie by `id` ascending, so the result order is fully deterministic. If
fewer than `topN` unflagged transactions exist, return all of them.

## Hard constraints (the actual point of the exercise)

- Use `Collectors.partitioningBy` to separate flagged from unflagged — not a manual `if`/`else`
  loop building two lists.
- Sort with `Comparator.comparing(...).reversed().thenComparing(...)` — reversed() must apply
  to the amount comparison only, and the tiebreaker must be a genuine second comparator, not a
  second sort pass.
- `BigDecimal` for amount — never `double`/`float`.

## Key questions before coding

- `Collectors.partitioningBy` always produces exactly two keys (`true`/`false`) in the result
  map, even if one bucket is empty. How is that different from `Collectors.groupingBy`, where a
  key you never encountered simply isn't present in the output map?
- Without an explicit tiebreaker, is the relative order of two equal-amount elements after
  `Stream.sorted()` actually guaranteed by the `Comparator` contract, or just incidentally
  consistent because of how the JDK's sort happens to be implemented?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: flagged transactions excluded even when they'd
   otherwise be the highest amount; result sorted descending and correctly capped at `topN`;
   a genuine amount tie broken deterministically by `id`.
2. Red → fail. Implement `topCleanTransactions()`. Green → pass.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — same result, plain loop + `Collections.sort` +
manual truncation instead of `partitioningBy`/`sorted`/`limit`. Same output, more places to get
the order of operations wrong.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q060_top_clean_transactions.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
