# q053 Review — Comparator Chaining: Multi-Field Sort

**Result: PASS — 2/2 tests**

---

## What You Got Right

- `Comparator.comparing(Employee::department).thenComparing(Comparator.comparing(Employee::salary).reversed())`
  — `reversed()` placed exactly right (on the salary sub-comparator, before it joins the
  chain), matching the reference solution's approach precisely. Department stays ascending,
  only salary flips.
- `.stream().sorted(...).toList()` — doesn't mutate the input, doesn't require it to be
  mutable. Correct choice over `employees.sort(...)`.
- **Your first test is a genuinely stronger discriminator than the reference test I wrote for
  this exercise.** Oscar (MATH, 70000) and Valentina (BIOLOGY, 70000) share the same salary —
  so the only thing that can put Valentina first is department ordering actually working
  correctly. A version that accidentally reversed department along with salary would put MATH
  before BIOLOGY here regardless of the salary tie, and this test would catch it immediately.
  My own reference test didn't include a cross-department salary tie, so yours exercises the
  exact bug this exercise is about more directly. Good instinct picking that case.
- Second test mirrors the non-mutation check correctly.

## Minor Notes (not deductions)

- Typo in the first test's method name (`should_return_an_empoyee_list_ordered...` —
  "empoyee"). Zero functional effect, just flagging since test names matter for readability
  when a suite grows.
- Test method naming uses `snake_case` for the first test, `camelCase` for the second —
  inconsistent within the same file. This project's convention elsewhere is `camelCase`
  throughout.

## Summary

Clean pass, and the salary-tie test case shows real understanding of what could break this
implementation, not just copying the happy path.

---

**Difficulty progression:** This was an explicit `next java medium` override (q052 had already
earned 2 consecutive medium passes and bumped the default to hard). The override doesn't reset
that — the next unforced `next java` still defaults to **hard**.
