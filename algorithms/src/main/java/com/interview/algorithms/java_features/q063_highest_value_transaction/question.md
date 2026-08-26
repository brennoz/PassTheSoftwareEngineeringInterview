# Exercise — Highest Value Transaction (Medium)

**Concepts:** `Stream.reduce` (no-identity overload → `Optional<T>`), `BinaryOperator.maxBy`
**Estimated time:** 20 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java`

---

## Problem

Payment/fraud domain: given a batch of transactions, find the single highest-value one.
Implement, in `MyAnswer.java`:

```java
public static Optional<Transaction> highestValueTransaction(List<Transaction> transactions)
```

Given `List<Transaction>` (`id`, `BigDecimal amount`), return the transaction with the highest
`amount`, wrapped in an `Optional` — the list can be empty, and there's no meaningful "zero
transaction" to fall back to in that case.

## Hard constraints (the actual point of the exercise)

- Use `Stream.reduce(BinaryOperator<T>)` — the single-argument, **no-identity** overload,
  which returns `Optional<T>`. Not the 2-arg `reduce(T identity, BinaryOperator<T> accumulator)`
  overload, and not a manual loop with a null/sentinel starting value.
- `BigDecimal` for amount — never `double`/`float`.

## Key questions before coding

- `Stream` has three `reduce` overloads. The 1-arg one (`BinaryOperator<T>`) returns
  `Optional<T>`. The 2-arg one (`T identity, BinaryOperator<T> accumulator`) returns `T`
  directly. Why does adding an identity value change the return type — what problem does
  supplying an identity actually solve, and why is there no safe identity value for "the
  transaction with the highest amount"?
- `BinaryOperator.maxBy(Comparator.comparing(Transaction::amount))` — used already in an
  earlier exercise as a `Collectors.toMap` merge function — is itself just a
  `BinaryOperator<Transaction>`. Can the exact same value be handed to `reduce()` instead?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: the highest-amount transaction is picked correctly
   even when it's neither first nor last in the list; an empty list produces `Optional.empty()`
   rather than throwing or returning a fabricated result.
2. Red → fail. Implement `highestValueTransaction()`. Green → pass.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — a manual loop with a nullable `highest` variable
instead of `reduce()`'s `Optional`-shaped "might not have a result yet."

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q063_highest_value_transaction.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
