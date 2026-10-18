# Review — Q078 Payment Search

**Date:** 2026-10-02  
**Result:** ✅ Pass  
**Tests:** 6/6 green

---

## Planted Issues — 6/6 Found and Fixed

| # | Issue | Fixed? | Notes |
|---|---|---|---|
| 1 | God-method `search()` — inline filter block, no reuse | ✅ | Extracted to `buildSpec()` |
| 2 | Duplicate filter logic in `findForRisk()` | ✅ | Eliminated by sharing `buildSpec()` |
| 3 | `findForRisk` type filter bug — unconditional `!p.getType().equals(criteria.type())` returned empty when type null | ✅ | Null guard in `buildSpec()` makes it structurally impossible |
| 4 | No type filter in `findForRisk` even when type specified | ✅ | Composed via `buildSpec(criteria).and(new FlaggedForRiskSpec())` |
| 5 | `double` for amount fields in `SearchCriteria` | ✅ | `BigDecimal` throughout |
| 6 | Mutable `SearchCriteria` with setters | ✅ | Immutable `record` |

---

## Design Quality

**`PaymentSpec` interface** — identical to reference. `@FunctionalInterface` with `and`/`or`/`negate`/`any()` as defaults. Correct choice of named interface over raw `Predicate<Payment>` — domain language reads better, composes the same way.

**`buildSpec()` identity element** — starting from `PaymentSpec.any()` (matches everything) and AND-chaining non-null criteria is exactly the right pattern. Avoids the null-accumulator anti-pattern (`if spec == null assign, else and-chain`). Matches reference.

**`findForRisk` composition** — `buildSpec(criteria).and(new FlaggedForRiskSpec())` — clean, and the structural fix means the type-filter omission bug is now impossible to reintroduce. You can't write a second query method that forgets to apply type filtering because type filtering lives in `buildSpec`.

**`Objects.requireNonNull`** — fail-fast at entry. A null criteria silently returning all payments would be a data leak in a compliance context. Good call.

**`AmountRangeSpec`** — correctly removed the spurious `compareTo(BigDecimal.ZERO) > 0` guard from the seeded code. That guard prevented filtering with `minAmount=0`, a valid boundary. Cleaner without it.

---

## Test Coverage

Good spread across the key behaviours:

| Test | What it proves |
|---|---|
| `search_filters_by_status_only` | Status spec works in isolation |
| `search_filters_by_amount_range_using_bigdecimal_precision` | BigDecimal comparisons correct |
| `search_with_empty_criteria_returns_all` | `PaymentSpec.any()` identity works — empty criteria = passthrough |
| `findForRisk_applies_type_filter_unlike_seeded_code` | Type filter wired correctly in `findForRisk` |
| `findForRisk_without_criteria_returns_only_flagged_payments` | The real null-type bug caught — this was the key test |
| `search_throws_when_criteria_is_null` | Null guard fires |

The commented-out test being uncommented and passing is the strongest signal — it directly exercises the seeded bug that wasn't caught by the pre-seeded test.

---

## Minor Notes (not deductions)

**`Collectors.toList()` → `.toList()`**: Java 21 project — `.toList()` (Java 16+) is shorter and returns an unmodifiable list. `Collectors.toList()` still works, just older style.

**`lenient()` private wrapper**: Works, 6 tests pass. Slightly indirect — `Mockito.lenient().when(...)` inline is more conventional and doesn't require a reader to look up what `lenient()` does. Not wrong, just unusual.

**No null-criteria test for `findForRisk`**: `search_throws_when_criteria_is_null` exists, but there's no matching test for `findForRisk(null)`. Both methods have the same `Objects.requireNonNull` guard — symmetrical test would be complete coverage.

---

## Technical Communication (pairing axis)

- Independently chose the Specification pattern — no prompting needed, correct call
- Confirmed type is optional for `findForRisk` when Leo asked — right answer, correct reasoning
- Responded to feedback on the `lenient()` method and fixed it
- Went quiet between the design decision and the finished implementation — narrating intent as you go ("I'm extracting AmountRangeSpec first, then wiring it in buildSpec") is the habit to build

---

## AWS / Distributed

The planted `repository.findAll()` issue points at the real production concern: in-memory filtering on a full table scan. At millions of payments, this is a full read of every row on every search call. The Specification pattern positions the codebase well for the next step: push the specs down to JPA `Specification<T>` / Querydsl predicates so the database does the filtering, not the application. Each leaf spec maps naturally to a `CriteriaBuilder` predicate — the interface contract doesn't change, only the implementation.
