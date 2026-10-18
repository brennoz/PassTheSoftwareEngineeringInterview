# q116 — Subtree of Another Tree

**Category:** Algorithms | **Difficulty:** Easy | **Ideal time:** 15 min

---

## Problem

Given the roots of two binary trees `root` and `subRoot`, return `true` if there is a subtree of `root` with the same structure and node values as `subRoot`, and `false` otherwise.

A subtree of a binary tree is a tree that consists of a node in that tree and all of that node's descendants.

---

## Examples

```
Input:  root=[3,4,5,1,2], subRoot=[4,1,2]   Output: true
Input:  root=[3,4,5,1,2,null,null,null,null,0], subRoot=[4,1,2]   Output: false
```

---

## Constraints

- 1 <= nodes in root <= 2000
- 1 <= nodes in subRoot <= 1000
- -10⁴ <= Node.val <= 10⁴

---

## Your task

Edit `MyAnswer.java` — implement `isSubtree(TreeNode root, TreeNode subRoot)`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
