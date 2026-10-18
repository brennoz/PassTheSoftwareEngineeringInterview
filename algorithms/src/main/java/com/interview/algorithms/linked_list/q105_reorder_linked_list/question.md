# q105 — Reorder Linked List

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 25 min

---

## Problem

Given the head of a singly linked list, reorder it in-place to the following pattern:

```
[0, n-1, 1, n-2, 2, n-3, ...]
```

You may not modify node values — only node pointers.

---

## Examples

```
Input:  [2,4,6,8]     Output: [2,8,4,6]
Input:  [2,4,6,8,10]  Output: [2,10,4,8,6]
```

---

## Constraints

- 1 <= list length <= 1000
- 1 <= Node.val <= 1000

## Strategy hint

Find the middle, reverse the second half, then merge both halves.

---

## Your task

Edit `MyAnswer.java` — implement `reorderList` (modifies in-place, returns void). The `ListNode` inner class is provided.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
