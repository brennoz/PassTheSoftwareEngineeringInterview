# Q018 — Team Formation Efficiency

**Category:** Algorithms — Two Pointers  
**Difficulty:** Medium  
**Ideal time:** 20 min

---

## Problem

Given an array `skill` of even length, form teams of exactly 2 participants such that:
- Every participant used exactly once
- All teams have the **same total skill** (sum of the pair)

Efficiency of team `[a, b]` = `a * b`

Return the **sum of efficiencies** of all teams, or `-1` if no valid arrangement exists.

---

## Examples

```
skill = [1, 2, 3, 2]
Output: 7

Pairs: [1,3], [2,2]  → all sum to 4 ✓
Efficiency: 1*3 + 2*2 = 3 + 4 = 7
```

```
skill = [2, 1, 1, 4, 3, 5]
Output: -1  (no valid equal-sum pairing)
```

---

## Constraints
- `1 ≤ skill.length ≤ 10^5` (always even)
- `1 ≤ skill[i] ≤ 10^5`
- Answer is unique if valid

---

## Skeleton
See `Solution.java`. Implement in `MyAnswer.java`.
