# Review — q067 Partitioned Payment Totals

**Result:** ✅ Pass  
**Tests:** 2/2 (no hints used)

---

## Implementation

```java
return transactions.stream()
        .collect(
            Collectors.partitioningBy(Transaction::successful,
                                      Collectors.reducing(BigDecimal.ZERO, Transaction::amount, BigDecimal::add))
        );
```

Exact match with reference solution.
- `partitioningBy(Transaction::successful, ...)` — correct predicate, method reference clean
- `reducing(BigDecimal.ZERO, Transaction::amount, BigDecimal::add)` — 3-arg form, returns `BigDecimal` directly (not `Optional<BigDecimal>`)
- No `double`/`float` anywhere

---

## Tests

**Test 1** — mixed list: true=1000 (200+300+500), false=1500 (100+1000+400). Math correct.  
Inputs are interleaved (not grouped by flag) — good: rules out an implementation that happens to work only when successful txs come first.

**Test 2** — all-failed list: true → `BigDecimal.ZERO`. This is the right test — it directly probes the `partitioningBy` vs `groupingBy` distinction (the exercise's core point). Clean.

---

## Gaps

**Missing: empty list.** Both keys → `BigDecimal.ZERO`. Not tested. Not a deduction — the two cases you covered are the more important ones — but worth noting.

**Name typo.** `"successfull"` (double `l`) in both test names and the `tx()` helper param. Harmless, but a typo in a test name is the one that stays in the git log forever.

---

## Key concept confirmed

You chose the 3-arg `reducing` (identity form) — returns `BigDecimal` directly. The 1-arg overload (`reducing(BigDecimal::add)`) would have returned `Optional<BigDecimal>`, making the method signature wrong. That distinction was the exercise's one live trap and you navigated it correctly.
