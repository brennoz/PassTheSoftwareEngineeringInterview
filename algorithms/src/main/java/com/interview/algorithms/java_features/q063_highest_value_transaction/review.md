# Review — q063 highest_value_transaction

**Result: ✅ Pass**

`transactions.stream().reduce(BinaryOperator.maxBy(Comparator.comparing(Transaction::amount)))`
— matches the reference exactly. Correct overload (no-identity, `Optional<T>`-returning),
`BigDecimal.valueOf(double)` used in the test helper (not `new BigDecimal(double)`).

## Gaps

1. **Empty-list case untested.** `question.md` explicitly named this as a case to cover — "an
   empty list produces `Optional.empty()` rather than throwing or returning a fabricated
   result" — and it's the entire reason the no-identity `reduce()` overload was the point of the
   exercise. The implementation handles it correctly (identical to the reference, which is
   verified for this case), so this is a coverage gap, not a functional bug — but it's the one
   behavior the exercise called out by name.

2. **Stray `@Test`** on line 25, directly above the private `tx()` helper. Compiles and runs
   fine — JUnit Jupiter's discovery excludes private methods, so it's silently a no-op, not an
   error — but it reads as an abandoned second test (presumably the empty-list case from gap 1)
   left half-written. Worth finishing it or deleting the stray annotation.

## What passed

Only functional test present: highest-amount transaction picked correctly, and it sits in the
*middle* of the input list (2500 is `tx("2", ...)` of three, not first or last) — rules out an
accumulator that just returns whichever argument came last. `BigDecimal.compareTo` used
correctly for the assertion, not `equals`.

No fix needed for the submitted logic — retry-worthy only if you want to close the two gaps
above.
