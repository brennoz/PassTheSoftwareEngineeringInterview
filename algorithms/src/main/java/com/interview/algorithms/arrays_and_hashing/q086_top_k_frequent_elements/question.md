# q086 — Top K Frequent Elements

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 20 min

---

## Problem

Given an integer array `nums` and an integer `k`, return the `k` most frequent elements. You may return the answer in any order.

---

## Examples

```
Input:  nums=[1,1,1,2,2,3], k=2    Output: [1,2]
Input:  nums=[1], k=1              Output: [1]
```

---

## Constraints

- 1 <= nums.length <= 100,000
- -10,000 <= nums[i] <= 10,000
- k is in the range [1, number of unique elements]
- It is guaranteed that the answer is unique.

## Recommended complexity

O(n) time using bucket sort (not O(n log n) heap).

---

## Your task

Edit `MyAnswer.java` — implement the `solve` method.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
