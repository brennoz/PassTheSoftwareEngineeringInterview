# q055 Review — Top Spender Per Department

**Result: PASS — 1/1 test**

---

## What You Got Right

- Nested collector composition matches the reference exactly:
  `groupingBy(department, collectingAndThen(groupingBy(employeeName, reducing(sum)),
  entrySet -> max by value -> key))`. Correctly used `collectingAndThen` to turn the
  per-employee-totals map into a single winning name before it lands in the outer map, rather
  than leaving the outer map's values as `Map<String, BigDecimal>`.
- Test data is genuinely well-designed, not just plausible-looking: in Biology, John Doe's
  three purchases sum to 1800 while Henrietta Bloom's two sum to 2500 — Henrietta wins despite
  neither having an individually dominant single purchase. In Physic, Einstein (6500) beats
  Samsa (4900) and a third competitor (3000). This actually exercises the "sum per employee
  first, THEN compare" requirement — a solution that mistakenly compared individual purchase
  amounts instead of per-employee totals would fail this test, which is the real thing this
  exercise is testing for.
- Multi-department coverage in a single test (Biology + Physic) confirms the outer grouping
  is independent per department.

## Minor Notes (not deductions)

- One test method. This project's usual guidance is 2–4 tests per class, and the single test
  here is doing real work (competitive multi-purchase totals across two departments), so it's
  not thin in substance — but an empty-list case (`topSpenderPerDepartment(List.of())` →
  empty map) would be a natural, cheap addition and is exactly the kind of edge case worth
  having as its own named test rather than folded into a bigger one.

## Summary

Correct three-collector composition on the first attempt, and the test data was specifically
constructed to distinguish "sum totals then compare" from "compare individual purchases" —
that's not an accident, it shows you understood exactly what could go wrong here.

---

**Difficulty progression:** Another explicit `next java medium` request. As noted after q053,
this doesn't reset the underlying default — an unforced `next java` still defaults to **hard**.
