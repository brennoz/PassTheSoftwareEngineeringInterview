# q103 — Linked List Cycle Detection

**Category:** Algorithms | **Difficulty:** Easy | **Ideal time:** 15 min

---

## Problem

Given the beginning of a linked list `head`, return `true` if there is a cycle in the linked list, otherwise return `false`.

A cycle exists if at least one node can be visited again by following the `next` pointer.

---

## Examples

```
Input:  [1,2,3,4] with tail → node[1]   Output: true
Input:  [1,2] with no cycle             Output: false
```

---

## Constraints

- 1 <= list length <= 1000
- -1000 <= Node.val <= 1000

---

## Your task

Edit `MyAnswer.java` — implement `hasCycle` using Floyd's Tortoise & Hare algorithm (O(1) space).
The `ListNode` inner class is provided.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
