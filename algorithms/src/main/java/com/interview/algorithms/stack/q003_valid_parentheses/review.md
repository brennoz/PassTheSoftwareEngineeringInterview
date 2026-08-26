# Review — Q003 Valid Parentheses

**Result: ✅ Pass**  
**Date:** 2026-05-26

---

## Rubric

| Criterion | Result | Notes |
|---|---|---|
| Correctness | ✅ | All cases handled correctly |
| Time complexity | ✅ | O(n) — single pass |
| Space complexity | ✅ | O(n) — stack holds at most n/2 open brackets |
| Edge cases | ✅ | Empty string, starts with `)`, unclosed `[` all handled |

---

## What went well

- Correct data structure choice: `ArrayDeque` over legacy `Stack`
- `stack.isEmpty()` guard before `pop()` — no crash on leading close bracket
- `return stack.isEmpty()` — caught the unclosed bracket case
- Self-caught bugs during iteration (wrong chars on lines 34-35)

---

## Gaps

- **No JUnit tests** — `MyAnswerTest.java` left empty. Should have at least: happy path, mismatched pair, unclosed bracket.
- **Noisy in-method comment block** — the ASCII scratch notes (`@ ( @`) should be deleted before submission.
- **`main` in solution class** — fine for local testing, remove before submitting in an interview context.

---

## Complexity Summary

- **Time:** O(n) — one iteration over the string
- **Space:** O(n) — stack size proportional to input (worst case: all open brackets)

---

## Optimal?

Yes. O(n) time / O(n) space is the theoretical minimum — you must read every character at least once, and must track unmatched opens.
