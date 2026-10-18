# q118 — Binary Tree DFS

**Category:** Algorithms | **Difficulty:** Easy | **Ideal time:** 20 min

---

## Problem

Given the root of a binary tree, implement the following DFS traversals:

1. `preOrderRecursive(TreeNode root)` — root → left → right (recursive)
2. `inOrderRecursive(TreeNode root)` — left → root → right (recursive)
3. `postOrderRecursive(TreeNode root)` — left → right → root (recursive)
4. `preOrderIterative(TreeNode root)` — same order, using an explicit stack
5. `inOrderIterative(TreeNode root)` — same order, using an explicit stack

---

## Examples

```
Tree: [1, 2, 3, 4, 5]

Pre-order:  [1, 2, 4, 5, 3]
In-order:   [4, 2, 5, 1, 3]
Post-order: [4, 5, 2, 3, 1]
```

---

## Constraints

- Number of nodes: 0 to 100
- -1000 <= Node.val <= 1000

---

## Your task

Edit `MyAnswer.java` — implement all five traversal methods.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
