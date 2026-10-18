# q120 — Validate Binary Search Tree

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 20 min

---

## Problem

Given the root of a binary tree, determine if it is a valid binary search tree (BST).

A valid BST is defined as follows:
- The left subtree of a node contains only nodes with keys **strictly less than** the node's key.
- The right subtree of a node contains only nodes with keys **strictly greater than** the node's key.
- Both the left and right subtrees must also be binary search trees.

---

## Examples

```
Input:  [2,1,3]         Output: true
Input:  [5,1,4,null,null,3,6]  Output: false  (4 < 5 but is in right subtree)
```

---

## Constraints

- Number of nodes: 1 to 10⁴
- -2³¹ <= Node.val <= 2³¹ - 1

---

## Strategy hint

Pass min and max bounds down the recursion. Use `Long.MIN_VALUE` and `Long.MAX_VALUE` to handle int boundary values.

---

## Your task

Edit `MyAnswer.java` — implement `isValidBST(TreeNode root)`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
