# q099 — Dijkstra's Shortest Path

**Category:** Algorithms | **Difficulty:** Hard | **Ideal time:** 35 min

---

## Problem

Given a weighted directed graph, implement Dijkstra's algorithm to find:
1. `shortestDistances(source)` — shortest distance from source to every node.
2. `shortestPath(source, target)` — the actual path (list of node IDs) from source to target.

---

## Example

```
Graph (4 nodes, directed):
  0 --2--> 1 --1--> 2 --3--> 3
  0 --4--> 2
  1 --5--> 3

shortestDistances(0) → [0, 2, 3, 6]
shortestPath(0, 3)   → [0, 1, 2, 3]
```

---

## Constraints

- Nodes are 0-indexed integers.
- Edge weights are non-negative.
- Unreachable nodes get distance `Integer.MAX_VALUE`.

## Recommended complexity

O((V + E) log V) using a min-heap with lazy deletion.

---

## Your task

Edit `MyAnswer.java` — implement both methods. Use the inner `record Edge(int to, int weight)`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
