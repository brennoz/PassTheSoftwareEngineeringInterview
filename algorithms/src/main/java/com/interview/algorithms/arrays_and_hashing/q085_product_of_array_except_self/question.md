# q085 — Product of Array Except Self

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 20 min

---

## Problem

Given an integer array `nums`, return an array `answer` such that `answer[i]` is equal to the product of all the elements of `nums` except `nums[i]`.

The product of any prefix or suffix of `nums` is guaranteed to fit in a 32-bit integer.

**You must write an algorithm that runs in O(n) time and without using the division operation.**

---

## Examples

```
Input:  [1, 2, 3, 4]    Output: [24, 12, 8, 6]
Input:  [-1, 1, 0, -3, 3]  Output: [0, 0, 9, 0, 0]
```

---

## Constraints

- 2 <= nums.length <= 100,000
- -30 <= nums[i] <= 30

## Recommended complexity

O(n) time, O(1) extra space (output array does not count).

---

## Your task

Edit `MyAnswer.java` — implement the `solve` method.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
