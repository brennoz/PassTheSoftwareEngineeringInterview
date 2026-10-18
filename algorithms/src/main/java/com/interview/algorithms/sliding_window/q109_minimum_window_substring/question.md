# q109 — Minimum Window Substring

**Category:** Algorithms | **Difficulty:** Hard | **Ideal time:** 30 min

---

## Problem

Given strings `s` and `t`, return the **shortest substring of `s`** that contains every character of `t` (including duplicates). If no such substring exists, return `""`.

---

## Examples

```
Input:  s="ADOBECODEBANC", t="ABC"   Output: "BANC"
Input:  s="OUZODYXAZV",   t="XYZ"   Output: "YXAZ"
Input:  s="x",            t="xy"    Output: ""
```

---

## Constraints

- 1 <= s.length <= 100,000
- 1 <= t.length <= s.length
- s and t contain uppercase and lowercase English letters.
- The output is guaranteed unique.

## Recommended complexity

O(n + |t|) using two frequency maps and have/need counters.

---

## Your task

Edit `MyAnswer.java` — implement `minWindow`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
