# q104 — Remove Nth Node From End of List

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 20 min

---

## Problem

Given the beginning of a linked list `head` and an integer `n`, remove the nth node from the **end** of the list and return the beginning of the list.

---

## Examples

```
Input:  head=[1,2,3,4], n=2   Output: [1,2,4]
Input:  head=[5], n=1         Output: []
Input:  head=[1,2], n=2       Output: [2]
```

---

## Constraints

- 1 <= list length <= 30
- 0 <= Node.val <= 100
- 1 <= n <= list length

## Recommended complexity

O(n) one-pass using two pointers.

---

## Your task

Edit `MyAnswer.java` — implement `removeNthFromEnd`. The `ListNode` inner class is provided.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
