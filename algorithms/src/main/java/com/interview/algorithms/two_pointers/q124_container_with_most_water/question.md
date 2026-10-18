# q124 — Container With Most Water

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 20 min

---

## Problem

Given an integer array `heights` where `heights[i]` is the height of the i-th vertical bar, choose any two bars to form a container. Return the maximum amount of water the container can store.

Area formula: `area = min(heights[l], heights[r]) × (r - l)`

---

## Examples

```
Input:  [1,8,2,5,4,8,3,7]   Output: 42
Input:  [4,13,1,2,15,9]     Output: 39
```

---

## Constraints

- 2 <= heights.length <= 10⁵
- 0 <= heights[i] <= 10⁴

---

## Strategy hint

Two converging pointers. Always advance the pointer at the **shorter** wall — moving the taller wall can only shrink width without a height gain (bottleneck is the shorter wall).

---

## Your task

Edit `MyAnswer.java` — implement `solve(int[] heights)`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
