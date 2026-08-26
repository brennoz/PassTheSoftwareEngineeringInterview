# q052 Review — Grouping + Downstream Collectors

**Result: PASS — 3/3 tests**

---

## What You Got Right

- Correct 3-arg `Collectors.groupingBy(classifier, downstream)` — didn't fall into the 2-arg
  `Map<String, List<Order>>` trap.
- `Collectors.reducing(BigDecimal.ZERO, mapper, BigDecimal::add)` — exactly the collector this
  exercise was testing for, and didn't reach for `summingDouble` despite it being the more
  discoverable option. Correct identity (`BigDecimal.ZERO`), correct combining function.
- Tests: multi-status grouping, single-order group, empty list — matches the reference suite's
  coverage exactly, and correctly used `assertEquals(0, actual.compareTo(expected))` for every
  `BigDecimal` comparison, never a direct `assertEquals` on two `BigDecimal`s.

## Minor Notes (not deductions)

- `order -> order.status()` / `order -> order.amount()` where `Order::status` / `Order::amount`
  (method references) would be more idiomatic — these are direct single-accessor-call lambdas,
  the textbook case a method reference exists for. Purely style, zero functional difference.

## Summary

Second clean pass in a row at `java` medium. Knew to skip `summingDouble` and reach for the
general-purpose `reducing` collector — the actual point of the exercise — without needing a
hint.

---

**Difficulty progression:** 2nd consecutive pass at `java` medium → bumping to **hard** for
the next `next java`.
