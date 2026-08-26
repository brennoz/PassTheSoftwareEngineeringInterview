# Exercise — Suspended Markets Summary (Medium)

**Concepts:** `Collectors.joining` (3-arg: delimiter/prefix/suffix), `Stream.filter`/`sorted`
**Estimated time:** 15 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java`

---

## Problem

Betting domain: format a human-readable summary of which markets are currently suspended.
Implement, in `MyAnswer.java`:

```java
public static String suspendedMarketsSummary(List<Market> markets)
```

Given `List<Market>` (`marketId`, `MarketStatus status` — `ACTIVE`/`SUSPENDED`/`CLOSED`),
return a string listing the `marketId`s of every `SUSPENDED` market, sorted alphabetically,
comma-separated, wrapped in square brackets — e.g. `[match-202, match-303]`. If none are
suspended, return `[]`.

## Hard constraints (the actual point of the exercise)

- Use `Collectors.joining(delimiter, prefix, suffix)` — the 3-arg overload — not the 1-arg
  overload plus manual string concatenation to add the brackets.
- Sort the suspended market IDs before joining, for deterministic, readable output.

## Key questions before coding

- What does `Collectors.joining(", ", "[", "]")` produce for an empty stream — is the
  prefix/suffix wrapping conditional on there being at least one element, or unconditional?
- If you built this with a `StringBuilder` and a loop instead, what's the boundary condition
  you'd need to get right to avoid a leading or trailing `", "`?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: mixed statuses (only `SUSPENDED` included,
   sorted, correctly bracketed); the zero-suspended-markets case producing `[]`.
2. Red → fail. Implement `suspendedMarketsSummary()`. Green → pass.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — a `StringBuilder` with an explicit
"is this the first element" check instead of `Collectors.joining` handling that internally.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q064_suspended_markets_summary.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
