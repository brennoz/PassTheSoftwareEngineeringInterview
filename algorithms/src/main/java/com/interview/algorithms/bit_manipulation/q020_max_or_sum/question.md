# Q020 — Max OR Sum

**Category:** Algorithms — Bit Manipulation  
**Difficulty:** Medium-Hard  
**Ideal time:** 25 min

---

## Problem

Given array `arr[]` of `n` integers and integer `k`.

You can perform at most `k` operations:
- Choose index `i`, replace `arr[i]` with `arr[i] * 2`

**OR-sum** = bitwise OR of all elements in the final array.

Return the **maximum possible OR-sum**.

---

## Examples

```
arr = [12, 9], k = 1

Option 1: double arr[0] → [24, 9] → 24 | 9 = 25
Option 2: double arr[1] → [12, 18] → 12 | 18 = 30

Output: 30
```

```
arr = [10, 1], k = 1
double arr[0] → [20, 1] → 20 | 1 = 21
Output: 21
```

---

## Constraints
- `1 ≤ n ≤ 10^5`
- `1 ≤ arr[i] ≤ 10^6`
- `1 ≤ k ≤ 11`

---

## Hint
Multiplying by 2 = left shift by 1. Think about which element, when shifted, sets the most new high bits in the OR result.

---

## Skeleton
See `Solution.java`. Implement in `MyAnswer.java`.
