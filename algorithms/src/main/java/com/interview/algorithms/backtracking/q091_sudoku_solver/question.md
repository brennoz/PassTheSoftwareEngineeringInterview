# q091 — Sudoku Solver

**Category:** Algorithms | **Difficulty:** Hard | **Ideal time:** 35 min

---

## Problem

Given a 9×9 Sudoku board (`int[][]`) with some cells pre-filled (non-zero) and some empty (0), fill all empty cells in-place so that:

- Each row contains digits 1–9 exactly once.
- Each column contains digits 1–9 exactly once.
- Each of the nine 3×3 sub-boxes contains digits 1–9 exactly once.

Return `true` if the board is solvable, `false` otherwise.

---

## Example

```
Input:
{5,3,0, 0,7,0, 0,0,0}
{6,0,0, 1,9,5, 0,0,0}
{0,9,8, 0,0,0, 0,6,0}
...

Output: true (board modified in-place with solution)
```

---

## Constraints

- Board is 9×9.
- `board[i][j]` is 0 (empty) or 1–9 (pre-filled).
- It is guaranteed the board has at most one solution.

---

## Your task

Edit `MyAnswer.java` — implement `sudokuSolver`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
