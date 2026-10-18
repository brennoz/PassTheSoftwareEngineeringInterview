# q123 — Valid Palindrome

**Category:** Algorithms | **Difficulty:** Easy | **Ideal time:** 15 min

---

## Problem

Given a string `s`, return `true` if it is a palindrome after:
1. Removing all non-alphanumeric characters
2. Converting all letters to lowercase

---

## Examples

```
Input:  "Was it a car or a cat I saw?"   Output: true
Input:  "tab a cat"                      Output: false
Input:  "!a!!"                           Output: true
Input:  " "                              Output: true
```

---

## Constraints

- 1 <= s.length <= 2×10⁵
- s contains only printable ASCII characters

---

## Strategy hint

Two converging pointers: skip non-alphanumeric with inner while loops, compare case-insensitively. Use `while` not `if` for the skip — `if` only skips one character at a time.

---

## Your task

Edit `MyAnswer.java` — implement `solve(String s)` and the helper `alphaNum(char c)`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
