# Transcript — q075_transaction_reconciliation

Appended automatically on every `review`. Each entry: what changed in `answer/` since the last
entry (diff summary), the test run result, and the in-character exchange.

---

## Pass 1

**Changed since seed:** `answer/MyAnswerTest.java` — one test added,
`reconcilesMultipleTransactionsWithTheSameCurrency`: 3 GBP transactions (100/500/400), rate
stubbed at 2, asserts total == 1000. `answer/TransactionReconciliationService.java` untouched
so far — still the exact seeded copy.

**Test run:** `mvn test -pl pairing-drill -Dtest=...answer.MyAnswerTest`
```
Tests run: 1, Failures: 1
expected: <0> but was: <1>
```
Real failure, but not the bug being hunted. `reconcileByCurrency` multiplies each amount by the
rate before summing: (100+500+400) × 2 = 2000, not 1000 — the test's expected value forgot the
rate multiplier. `compareTo` returning a positive value (shown as `<1>`) means the actual total
exceeded the expected one.

**Exchange:** B caught the arithmetic mismatch by recomputing by hand rather than trusting the
failure message alone. A flagged a separate, not-yet-relevant risk: this test's small size (3
elements) may or may not currently expose the `parallelStream`/shared-`HashMap` race depending
on JVM scheduling — worth remembering once the real concurrency fix is in place, since a test
that only sometimes fails is exactly the "intermittent" symptom from the ticket.

**Open:** expected value not yet corrected; no fix attempted yet on
`TransactionReconciliationService.java` itself.

---

## Pass 2

**Changed since pass 1 — all four planted bugs addressed:**
- `RateProvider.getRate` returns `Optional<BigDecimal>`; `rateFor` uses `orElseThrow(() -> new
  MissingRateException(...))`. Expected value corrected to 2000 (rate accounted for).
- `reconcileByCurrency` rewritten with `Collectors.groupingBy` + `reducing` — no shared
  mutable `HashMap`, no `parallelStream`. Old commented-out version fully removed, not left
  dead in the file.
- `auditAsync` now returns `CompletableFuture<Void>`; futures are collected and joined via
  `CompletableFuture.allOf(...).join()`, with `.exceptionally()` logging any failure instead of
  letting it vanish.
- `amountsById` pre-checks for duplicate IDs via `groupingBy` + `counting()` and throws
  `DuplicateTransactionException` naming the actual duplicated ID.
- Currency-grouping ambiguity (raised in the earlier exchange) — still parked, not resolved,
  consistent with the explicit deferral agreed then.

**Test run:** `mvn test -pl pairing-drill -Dtest=...answer.MyAnswerTest`
```
Tests run: 5, Failures: 0, Errors: 0
```
Green, 5/5 — including the log line `"Audit failed for transaction tx-2: ..."` printing from
inside `.exceptionally()`, confirming the handler genuinely fires rather than just compiling.

**Flagged, not yet fixed:** `answer/TransactionReconciliationService.java` and
`answer/MyAnswerTest.java` both `import
com.interview.pairingdrill.q075_transaction_reconciliation.solution.DuplicateTransactionException`
directly from `solution/`, instead of defining the exception inside `answer/`. Breaks the
self-contained-package boundary the folder shape depends on — `solution/` is meant to be
reference, not a dependency of `answer/`.

---

## Pass 3

**Changed since pass 2:** `answer/DuplicateTransactionException.java` added — own copy, correct
package, cross-package import to `solution/` removed from both
`TransactionReconciliationService.java` and `MyAnswerTest.java`. `answer/` is now fully
self-contained.

**Test run:** `mvn test -pl pairing-drill` (full module)
```
answer.MyAnswerTest:   Tests run: 5, Failures: 0, Errors: 0
solution.SolutionTest: Tests run: 5, Failures: 0, Errors: 0
```
Both green, whole module exit 0.

**State at this point:** all four originally-planted bugs fixed and covered by tests; dead
code removed; self-containment restored. Only remaining open item across the whole session:
the currency-grouping semantic ambiguity, explicitly parked earlier and not revisited.

---
