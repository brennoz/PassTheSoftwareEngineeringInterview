# q100 — Directed Acyclic Graph (DAG) API

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 30 min

---

## Problem

Implement an in-memory **Directed Acyclic Graph** where nodes are identified by `String` names. Support:

- `addNode(node)` — add a node.
- `addEdge(parent, child)` — add a directed edge (reject if it would create a cycle).
- `hasEdge(parent, child)` — returns true if direct edge exists.
- `childrenOf(node)` — returns direct children.
- `parentsOf(node)` — returns direct parents.
- `descendantsOf(node)` — returns all reachable descendants (transitive closure).
- `ancestorsOf(node)` — returns all ancestors (transitive closure).

---

## Example

```
Graph:
  Fluffy → Rabbit
  Animal → Rabbit
  Animal → Rat
  Rat → Grey Rat, Black Rat, Roland Rat

descendantsOf("Animal") → {Rabbit, Rat, Grey Rat, Black Rat, Roland Rat}
ancestorsOf("Roland Rat") → {Rat, Animal}
```

---

## Constraints

- No self-loops or cycles.
- Duplicate edges silently ignored.

---

## Your task

Edit `MyAnswer.java` — implement the DAG API.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
