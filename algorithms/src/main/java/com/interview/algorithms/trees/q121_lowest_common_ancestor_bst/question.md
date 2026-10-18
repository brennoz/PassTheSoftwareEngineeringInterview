# q121 — Lowest Common Ancestor in BST

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 15 min

---

## Problem

Given a binary search tree (BST), find the lowest common ancestor (LCA) node of two given nodes `p` and `q`.

The LCA is defined as the lowest node in the tree that has both `p` and `q` as descendants (a node can be a descendant of itself).

---

## Examples

```
Input:  root=[6,2,8,0,4,7,9,null,null,3,5], p=2, q=8   Output: 6
Input:  root=[6,2,8,0,4,7,9,null,null,3,5], p=2, q=4   Output: 2
Input:  root=[2,1], p=2, q=1                            Output: 2
```

---

## Constraints

- Number of nodes: 2 to 10⁵
- -10⁹ <= Node.val <= 10⁹
- All values are unique
- p != q, both p and q exist in the BST

---

## Strategy hint

Exploit the BST property: if both nodes are less than root, go left. If both are greater, go right. Otherwise root is the LCA.

---

## Your task

Edit `MyAnswer.java` — implement `lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q)`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
