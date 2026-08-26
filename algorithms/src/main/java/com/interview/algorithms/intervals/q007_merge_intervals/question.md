# Q007 — Merge Intervals

**Category:** Algorithms  
**Difficulty:** Medium  
**Ideal Time:** 20 min

---

## Problem

Given an array of intervals where `intervals[i] = [start, end]`, merge all overlapping intervals and return an array of the non-overlapping intervals that cover all the intervals in the input.

---

## Examples

```
Input:  [[1,3],[2,6],[8,10],[15,18]]  →  [[1,6],[8,10],[15,18]]
Input:  [[1,4],[4,5]]                 →  [[1,5]]    ← touching = overlapping
Input:  [[1,4],[0,4]]                 →  [[0,4]]    ← unsorted input
Input:  [[1,4],[2,3]]                 →  [[1,4]]    ← one fully inside another
Input:  [[1,2]]                       →  [[1,2]]
```

---

## Constraints

- `1 <= intervals.length <= 10^4`
- `intervals[i].length == 2`
- `0 <= start <= end <= 10^4`
- Input may be unsorted

---

## What to Implement

```java
public int[][] merge(int[][] intervals)
```

---

## Skeleton

See `Solution.java` for reference.  
Write your solution in `MyAnswer.java`.  
Write tests in `MyAnswerTest.java`.
