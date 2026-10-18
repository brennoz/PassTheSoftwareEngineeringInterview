# q094 — Counting Bits

**Category:** Algorithms | **Difficulty:** Easy | **Ideal time:** 15 min

---

## Problem

Given an integer `n`, return an array `output` where `output[i]` is the number of `1` bits in the binary representation of `i`, for all `i` in the range `[0, n]`.

---

## Examples

```
Input:  n=4
Output: [0,1,1,2,1]

Explanation:
0 → 0     (0 ones)
1 → 1     (1 one)
2 → 10    (1 one)
3 → 11    (2 ones)
4 → 100   (1 one)
```

---

## Constraints

- 0 <= n <= 1000

## Recommended complexity

O(n) time using dynamic programming (avoid calling Integer.bitCount per value).

---

## Your task

Edit `MyAnswer.java` — implement `countBits`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
