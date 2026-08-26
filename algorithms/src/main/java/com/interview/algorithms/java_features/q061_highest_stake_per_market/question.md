# Exercise — Highest Stake Per Market (Medium)

**Concepts:** `Collectors.toMap` (3-arg, with merge function), `BinaryOperator.maxBy`,
`Comparator.comparing`
**Estimated time:** 20 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java`

---

## Problem

Betting domain: a customer can place multiple bets on the same market (re-betting, top-ups,
whatever the reason). Implement, in `MyAnswer.java`:

```java
public static Map<String, Bet> highestStakePerMarket(List<Bet> bets)
```

Given `List<Bet>` (`id`, `marketId`, `BigDecimal stake`), return a `Map<String, Bet>` keyed by
`marketId`, where the value is the bet with the **highest stake** for that market. Markets with
only one bet just map straight through.

## Hard constraints (the actual point of the exercise)

- Use `Collectors.toMap` with its 3-argument overload (key mapper, value mapper, merge
  function) — not the 2-arg overload, and not a manual `Map` + loop.
- The merge function must genuinely compare stakes (`BinaryOperator.maxBy` +
  `Comparator.comparing`), not just "keep the first" or "keep the second" argument — order in
  the input list must not affect which bet wins.
- `BigDecimal` for stake — never `double`/`float`.

## Key questions before coding

- `Collectors.toMap`'s 2-arg overload (no merge function) — what does it do the moment it hits
  a second value for a key it's already seen? Why is that the wrong behaviour here?
- `BinaryOperator<T>` is a `BiFunction<T, T, T>` — given two colliding `Bet` values, what does
  it need to return, and how does `Comparator.comparing(Bet::stake)` let `maxBy` make that
  decision without you writing an `if`/`else` yourself?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: two bets on the same market, higher stake wins;
   the same case with input order reversed (higher stake still wins — proves the merge function
   is value-based, not order-based); a market with a single bet is unaffected.
2. Red → fail. Implement `highestStakePerMarket()`. Green → pass.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — same result via `containsKey`/manual compare instead
of a merge function. Same output, but the "which one wins" decision isn't reusable as a value
the way `BinaryOperator.maxBy(...)` is.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q061_highest_stake_per_market.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
