# q089 — Valid Sudoku

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 25 min

---

## Problem

Given a 9×9 Sudoku board of characters `'1'`–`'9'` and `'.'` (empty cell), determine whether the board is currently valid:

- Each **row** contains digits 1-9 with no repeats.
- Each **column** contains digits 1-9 with no repeats.
- Each of the nine **3×3 boxes** contains digits 1-9 with no repeats.

The board does not need to be full or solvable — only the filled cells need to satisfy the constraints.

---

## Example

```
Valid board:
['1','2','.','.','3','.','.','.','.'],
['4','.','.','5','.','.','.','.','.'],
...
→ true
```

---

## Constraints

- `board.length == 9`
- `board[i].length == 9`
- `board[i][j]` is `'1'`–`'9'` or `'.'`

## Recommended complexity

O(n²) time (n=9, so O(81)), O(n²) space.

---

## Your task

Edit `MyAnswer.java` — implement the `solve` method.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
