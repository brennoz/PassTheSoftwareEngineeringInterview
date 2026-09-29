# Exercise — Compliance Filter (Medium)

**Lessons:** [0002 Lambdas & Functional Interfaces](../../../../../topics/concepts/java/0002-lambdas.html)
**Concepts:** `Predicate<T>.and()` / `.or()` composition · `stream().reduce(identity, Predicate::and)` · choosing the correct identity element
**Estimated time:** 20 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see `solution/ImperativeApproach.java`

---

## Domain

A betting operator runs compliance checks before accepting a bet. Each check is expressed as a
`Predicate<Bet>` that returns `true` if the bet is **safe** (for `approvedBets`) or `true` if
the bet **should be blocked** (for `blockedBets`). The operator wants to compose arbitrary rule
lists without touching the predicate logic itself.

```java
public record Bet(String betRef, String bettor, BigDecimal stake, String region, boolean selfExcluded) {}
```

---

## Problem

Implement **two methods** in `MyAnswer.java`:

```java
// Returns bets that pass ALL safety rules.
// rules.get(i).test(bet) == true means "this bet is safe under rule i".
// Empty rules list → all bets are approved.
public static List<Bet> approvedBets(List<Bet> bets, List<Predicate<Bet>> rules)

// Returns bets blocked by AT LEAST ONE risk rule.
// rules.get(i).test(bet) == true means "this bet is a risk under rule i".
// Empty rules list → no bets are blocked.
public static List<Bet> blockedBets(List<Bet> bets, List<Predicate<Bet>> rules)
```

---

## Hard constraints (the actual point of the exercise)

- `approvedBets`: combine all rules into a **single** `Predicate<Bet>` using `Predicate::and` via
  `stream().reduce(...)`. Do NOT call `.filter(rule)` in a loop.
- `blockedBets`: same discipline — combine with `Predicate::or` before filtering.
- Both methods must behave correctly when `rules` is empty — the identity element is the key.
- No `for` / `while` loops in either method.

---

## Key questions before coding

- `stream().reduce(identity, accumulator)` — what is the correct identity for `.and()`?
  What does it mean if no rules are provided?
- If you used `reduce(Predicate::and)` (no identity, returns `Optional<Predicate<Bet>>`), what
  happens when `rules` is empty? How would you handle it?
- `Predicate::and` as a method reference — which overload does the compiler resolve it to?
  Is it `BinaryOperator<Predicate<Bet>>` or something else?

---

## TDD workflow

1. Write `MyAnswerTest.java` in the test directory. Cover:
   - `approvedBets` — multiple rules, some bets fail one rule (verify those are excluded)
   - `approvedBets` — empty rules list → full list returned
   - `blockedBets` — multiple rules, some bets trigger exactly one rule (verify those are included)
   - `blockedBets` — empty rules list → empty list returned
2. Red → fail. Implement. Green → pass.

---

## Bonus

Add a third method (no reference in `Solution.java` — design it yourself):

```java
// Returns bets that fail AT LEAST ONE safety rule (the complement of approvedBets).
public static List<Bet> rejectedBets(List<Bet> bets, List<Predicate<Bet>> rules)
```

Hint: it is a one-liner given a working `approvedBets`.

---

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q070_predicate_composition.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
