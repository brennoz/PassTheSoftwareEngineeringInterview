# Q029 — 3Sum

**Category:** Algorithms — Two Pointers  
**Difficulty:** Hard  
**Ideal time:** 20 min  
**Lesson:** [0001 Two Pointers](../../../../../../topics/algo/two-pointers/0001-two-pointers.html)

---

## Problem

Given an integer array `nums`, return all **unique triplets** `[nums[i], nums[j], nums[k]]`
such that `i != j`, `i != k`, `j != k`, and `nums[i] + nums[j] + nums[k] == 0`.

The solution set must not contain duplicate triplets.

---

## Examples

```
Input:  nums = [-1, 0, 1, 2, -1, -4]
Output: [[-1,-1,2], [-1,0,1]]
```

```
Input:  nums = [0, 1, 1]
Output: []
```

```
Input:  nums = [0, 0, 0]
Output: [[0,0,0]]
```

---

## Constraints

- `3 ≤ nums.length ≤ 3000`
- `-10^5 ≤ nums[i] ≤ 10^5`
- Output order within triplets and between triplets does not matter

---

## What to implement

Implement `threeSum(int[] nums)` in `MyAnswer.java`.

Write tests in `MyAnswerTest.java`:
- Basic case: `[-1,0,1,2,-1,-4]` → `[[-1,-1,2],[-1,0,1]]`
- All zeros: `[0,0,0]` → `[[0,0,0]]`
- No valid triplet: `[1,2,3]` → `[]`

---

## Hint (think before reading)

Sort the array. Fix index `i`, then use Two Pointers from `i+1` to `n-1`.
The hard part is skipping duplicates correctly — three places where you must skip.
