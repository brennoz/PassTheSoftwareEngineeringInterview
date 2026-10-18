# q084 — Group Anagrams

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 25 min

---

## Problem

Given an array of strings `strs`, group all anagrams together into sublists. You may return the output in any order.

An anagram is a string that contains the exact same characters as another string, but the order of the characters can be different.

---

## Examples

```
Input:  ["act","pots","tops","cat","stop","hat"]
Output: [["hat"],["act","cat"],["stop","pots","tops"]]

Input:  ["x"]     Output: [["x"]]
Input:  [""]      Output: [[""]]
```

---

## Constraints

- 1 <= strs.length <= 1000
- 0 <= strs[i].length <= 100
- strs[i] consists of lowercase English letters only

## Recommended complexity

O(m × n) time, O(m) space — where m = number of strings, n = length of longest string.

---

## Your task

Edit `MyAnswer.java` — implement the `groupAnagrams` method.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
