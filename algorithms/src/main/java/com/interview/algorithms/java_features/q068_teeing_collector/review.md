# Review — q068 Teeing Collector

**Result: PASS**  
**Difficulty: Medium**

---

## Correctness

`buildReport` is correct. Single stream pass, `Collectors.teeing` wired correctly:

- **Downstream 1:** `Collectors.filtering(won, reducing(ZERO, payout, add))` — sums payouts for winners only, returns `BigDecimal.ZERO` when none (3-arg reducing overload, identity form — no `Optional`).
- **Downstream 2:** `Collectors.counting()` — counts all settlements (won + lost), correct for `settledCount`.
- **Merger:** `SettlementReport::new` — constructor reference, matches `(BigDecimal, Long) → SettlementReport`.

Parenthesis structure is now correct (previous version had `filtering` unclosed, passing `counting` and merger as extra args to it — compile error).

---

## Rubric

| Criterion | Result |
|---|---|
| `Collectors.teeing` used (single pass) | ✓ |
| `Collectors.filtering` inside teeing (not pre-filter on stream) | ✓ |
| 3-arg `reducing` (returns `T`, not `Optional<T>`) | ✓ |
| `BigDecimal.ZERO` identity (no null, no Optional) | ✓ |
| No `double`/`float` | ✓ |
| `settledCount` counts all (not just winners) | ✓ |
| Idiomatic predicate (`settlement.won()` not `== Boolean.TRUE`) | ✓ |

---

## Test Coverage

One test: mixed list, verifies both `totalWinnerPayout` and `settledCount` independently. `BigDecimal` compared via `compareTo` (correct — scale-insensitive).

**Missing (not blocking, but worth adding):**
- All-lost list → `totalWinnerPayout` = ZERO, `settledCount` = N (the pre-filter trap test — proves you didn't pre-filter with `.filter()` before teeing)
- Empty list → both fields zero

The question explicitly asked for these two cases. They would have caught the parenthesis bug earlier (compile failure = red immediately).

---

## Key concept check

`Collectors.filtering(predicate, downstream)` vs `.filter()` on stream:

- `.filter()` before `.collect()` removes elements from ALL downstream collectors — `settledCount` would only count winners, wrong.
- `Collectors.filtering` scopes the predicate to ONE downstream inside `teeing` — the other downstream (counting) still sees all elements.

That distinction is the entire point of this exercise. Implementation shows it's understood.

---

## What to fix before next time

Add the two missing test cases (all-lost, empty). They're cheap to write and they lock in the core invariant.
