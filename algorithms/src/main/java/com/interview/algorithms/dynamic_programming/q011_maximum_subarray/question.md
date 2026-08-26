# Q011 — Maximum Subarray

**Category:** Algorithms  
**Difficulty:** Medium  
**Ideal Time:** 20 min

---

## Problem

Given an integer array `nums`, find the subarray with the largest sum and return that sum.

A **subarray** is a contiguous part of the array.

---

## Examples

```
Input:  [-2,1,-3,4,-1,2,1,-5,4]  →  6    (subarray [4,-1,2,1])
Input:  [1]                       →  1
Input:  [5,4,-1,7,8]              →  23   (entire array)
Input:  [-1,-2,-3]                →  -1   (single element — must pick at least one)
```

---

## Constraints

- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`
- Must return the **sum**, not the subarray itself
- Array contains at least one element
- All-negative array: return the largest (least negative) single element

---

## What to Implement

```java
public int maxSubArray(int[] nums)
```

---

## Skeleton

See `Solution.java` for reference.  
Write your solution in `MyAnswer.java`.  
Write tests in `MyAnswerTest.java`.

---

## Follow-up (mention in interview if time allows)

Can you also return the start and end indices of the subarray?
