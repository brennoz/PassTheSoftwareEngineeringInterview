# q106 — Fibonacci

**Category:** Algorithms | **Difficulty:** Easy | **Ideal time:** 15 min

---

## Problem

Compute the nth Fibonacci number using three strategies:

- `IMPERATIVE` — iterative O(n) O(1) space
- `RECURSIVE` — naive recursion O(2^n) — for comparison
- `DYNAMIC` — memoized recursion O(n)

Use `BigInteger` to handle large values without overflow.

---

## Examples

```
fibonacci(0, IMPERATIVE) → 0
fibonacci(1, IMPERATIVE) → 1
fibonacci(10, IMPERATIVE) → 55
fibonacci(41, DYNAMIC) → 165580141
```

Sequence: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, ...

---

## Constraints

- 0 <= n <= 50 for RECURSIVE (exponential time)
- No limit for IMPERATIVE / DYNAMIC

---

## Your task

Edit `MyAnswer.java` — implement `fibonacci(int n, FibonacciMode mode)`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
