# q098 — DFS Graph Traversal

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 20 min

---

## Problem

Given a graph node (with value and a list of neighbors), return all node values visited in **Depth-First Search** order starting from the root.

---

## Example

```
Graph:
5 -- 7 -- 10
     |
     4
5 -- 9 -- 14

DFS from 5 (one valid order): [5, 7, 10, 4, 9, 14]
```

---

## Constraints

- Graph may have cycles → use a visited set.
- Node values are integers.
- Implement recursively.

---

## Your task

Edit `MyAnswer.java` — implement `traverse`. The `Node` inner class is already defined.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
