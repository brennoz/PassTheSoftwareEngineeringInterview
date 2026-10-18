# q097 — BFS Graph Traversal

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 20 min

---

## Problem

Given a graph node (with value and a list of neighbors), return all node values visited in **Breadth-First Search** order starting from the root.

---

## Example

```
Graph:
5 -- 7 -- 10
     |
     4
5 -- 9 -- 14

BFS from 5: [5, 7, 9, 10, 4, 14]  (order may vary within levels)
```

---

## Constraints

- Graph may not be a tree (cycles possible → use visited set).
- Node values are integers.

---

## Your task

Edit `MyAnswer.java` — implement `traverse`. The `Node` inner class is already defined there.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
