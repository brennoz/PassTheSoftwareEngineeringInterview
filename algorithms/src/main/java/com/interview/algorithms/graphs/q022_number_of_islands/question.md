# Q022 — Number of Islands

**Category:** Algorithms — Graphs / DFS  
**Difficulty:** Medium  
**Ideal time:** 20 min

---

## Problem

Given a 2D grid of `'1'` (land) and `'0'` (water), count the number of **islands**.

An island is surrounded by water and formed by connecting adjacent land cells **horizontally or vertically** (4 directions only — no diagonals).

You may assume all four edges of the grid are surrounded by water.

---

## Examples

```
grid = [
  ["1","1","1","1","0"],
  ["1","1","0","1","0"],
  ["1","1","0","0","0"],
  ["0","0","0","0","0"]
]
Output: 1
```

```
grid = [
  ["1","1","0","0","0"],
  ["1","1","0","0","0"],
  ["0","0","1","0","0"],
  ["0","0","0","1","1"]
]
Output: 3
```

---

## Constraints
- `1 ≤ rows, cols ≤ 300`
- `grid[i][j]` is `'0'` or `'1'`

---

## What to implement
```java
public int numIslands(char[][] grid)
```

Write tests in `MyAnswerTest.java` first (TDD).  
Implement in `MyAnswer.java`.
