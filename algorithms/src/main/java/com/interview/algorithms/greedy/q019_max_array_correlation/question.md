# Q019 — Max Array Correlation

**Category:** Algorithms — Greedy  
**Difficulty:** Medium  
**Ideal time:** 20 min

---

## Problem

For two arrays `a` and `b` of equal length, **array correlation** = sum of all `b[i]` where `b[i] > a[i]`.

Given fixed `a[]` and rearrangeable `b[]`, return the **maximum possible array correlation**.

You may NOT rearrange `a`.

---

## Examples

```
a = [1, 2, 3, 4, 5]
b = [3, 5, 4, 6, 2]

Rearrange b → [2, 3, 4, 5, 6]
Every b[i] > a[i] → correlation = 2+3+4+5+6 = 20
```

```
a = [1, 4, 2, 1, 3]
b = [2, 3, 1, 2, 2]
Output: 7
```

---

## Constraints
- `1 ≤ n ≤ 2×10^5`
- `1 ≤ a[i], b[i] ≤ 10^9`

---

## Skeleton
See `Solution.java`. Implement in `MyAnswer.java`.
