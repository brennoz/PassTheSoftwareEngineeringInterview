# q107 — Recursion Patterns

**Category:** Algorithms | **Difficulty:** Easy | **Ideal time:** 10 min

---

## Problem

Implement the `head` recursive pattern:

Given `n`, print the call stack descending `n → 0`, then print the backtracking phase ascending `1 → n`.

---

## Example

```
head(3) prints:
  Calling with n=3
  Calling with n=2
  Calling with n=1
  Calling with n=0
  (Backtracking) n=1
  (Backtracking) n=2
  (Backtracking) n=3
```

---

## Key insight

Statements BEFORE the recursive call execute top-down (pre-order).
Statements AFTER the recursive call execute bottom-up (backtracking / post-order).

---

## Your task

Edit `MyAnswer.java` — implement `head(int n)`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
