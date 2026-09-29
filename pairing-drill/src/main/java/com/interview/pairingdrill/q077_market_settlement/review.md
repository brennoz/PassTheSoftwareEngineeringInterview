# Review — q077 Market Settlement

**Category:** pairing-drill · Type safety / primitive obsession · Sport betting · Medium  
**Tests at close:** 2/2 ✅ (answer), 3/3 ✅ (solution)

---

## Rubric — Five Axes

---

### 1. Reading Unfamiliar Code

**Strong.** Oriented quickly: identified the incident root cause (case-sensitive `String.equals()` on `winningSide`), spotted the maintainability complaint (`MarketStatus` hardcoded strings), and traced both to the right layer. Did not chase symptoms — went straight to the type boundary.

One gap: `Stake` was read as a mutable value class and left as one. The solution converts it to an immutable `record` — Stake has no identity beyond its data (a placed bet's stake can never legitimately change), so setters on it are a design smell. Easy to miss under time pressure, but in a pairing interview reading a collaborator's value objects for mutable state is a useful habit.

---

### 2. Technical Communication

**Good.** The fixes are legible and would pass code review. The `why` behind each choice is defensible when asked — enum prevents the incident, `BigDecimal` prevents payout rounding.

One subtlety worth knowing for interviews: **enum comparison should use `==` not `.equals()`**. Your service used `.equals(MarketStatus.OPEN)` — it works (enums delegate `.equals()` to reference equality internally), but `==` is idiomatic because it makes the singleton nature explicit and avoids any accidental null-safe confusion. The solution comments this directly. If an interviewer spots it and asks, the answer is: "enums are JVM singletons, `==` is semantically correct and slightly more expressive."

On `java.util.Currency` vs `String` for currency: choosing the richer type is a reasonable instinct — it validates ISO 4217 at construction. In practice it creates friction with JPA (custom `@Converter`) and Jackson (custom deserialiser), so most codebases use a validated `String` or a simple `Currency` value object wrapping a string. Neither is wrong; being able to name the tradeoff is what matters.

---

### 3. Prioritization

**Good.** Fixed the incident cause first (type safety on `side`), then the `MarketStatus` enum, then `BigDecimal` throughout. That's the right order — incident mitigation, then maintainability, then correctness of calculations. `@Transactional` added unprompted, which shows awareness of the multi-step nature of settlement.

The `Stake` → `record` refactor was the one thing left on the table, but it's lower priority than the other changes, so leaving it is reasonable under time pressure.

---

### 4. Balanced Engineering

**Solid with one gap.** All changes are proportionate and justified. No over-engineering. The for-loop payout accumulation works correctly — the solution uses a stream + `reduce` which is more idiomatic Java 8+, but the loop is not wrong.

The real gap is **tests don't assert `totalPayout`**. That value is the entire financial output of the operation. A code reviewer would catch this immediately:

```java
// Missing in your tests:
assertEquals(0, market.getTotalPayout().compareTo(new BigDecimal("17.50")));
```

The solution also has a dedicated `total_payout_is_zero_when_no_bets_are_on_winning_side` test — specifically to catch a bug where losing bets accidentally contribute to the sum. Without it, a naive bug like `bets.forEach(b -> totalPayout.add(...))` (forgetting the `isWon()` filter) would pass your tests.

Also: the guard test doesn't verify that `betRepository.findByMarketId` was never called after the exception. The intent of the guard is to reject early — if it throws after loading bets, it's still wasted DB work. The solution pins this with:

```java
Mockito.verify(betRepository, Mockito.never()).findByMarketId(Mockito.anyString());
```

---

### 5. Leading Safely

**Good.** Changes were incremental and the seeded code structure was preserved where appropriate (constructor injection, service boundary). Did not introduce unnecessary abstractions. `@Transactional` was added correctly without over-explaining it.

One thing to be ready to explain in a real pairing: `@Transactional` in a unit test with a manually-constructed service has no effect — there's no Spring proxy wrapping the call. The annotation is correct production code; just worth knowing it's not exercised by the unit test. If an interviewer asks "how do you verify the transactional behaviour?" the answer is: integration test with a real DB or `@SpringBootTest`.

---

## Summary

Strong fundamentals on the core skill (type safety, `BigDecimal`). The incident root cause was identified and fixed correctly. The two things to internalise going forward:

1. **Test the financial output, not just the state flags.** `isWon()` tells you the logic ran. `totalPayout` tells you the money is right.
2. **Value objects should be immutable.** When you see a collaborator with setters and no identity, consider whether a `record` is the right shape.

---

## Planted Issues — Debrief

| # | Issue | Found? |
|---|---|---|
| 1 | `String winningSide` — case-sensitive `.equals()` caused zero-payout incident | ✅ Fixed |
| 2 | `String status` on `Market` — magic strings for state machine | ✅ Fixed |
| 3 | `double odds`, `double amount` on `Bet`/`Stake` — floating-point payout errors | ✅ Fixed |
| 4 | `String currency` — unvalidated, no type | ✅ Fixed (with `Currency` type — valid choice) |
| 5 | `Stake` mutable class — value object with setters, should be `record` | ❌ Missed |

4/5 planted issues found and fixed. Missing: `Stake` → immutable record.
