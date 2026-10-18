# q108 — Longest Repeating Character Replacement

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 25 min

---

## Problem

Given a string `s` of only uppercase English characters and an integer `k`, you may replace **up to k** characters with any other uppercase letter.

Return the length of the **longest substring** that contains only one distinct character after at most k replacements.

---

## Examples

```
Input:  s="XYYX", k=2      Output: 4   (replace both Y's or both X's)
Input:  s="AAABABB", k=1   Output: 5   (window "AAABA": replace B → "AAAAA")
```

---

## Constraints

- 1 <= s.length <= 100,000
- s contains only uppercase English letters ('A'–'Z')
- 0 <= k <= s.length

## Recommended complexity

O(n) using the lazy maxFreq sliding window.

---

## Your task

Edit `MyAnswer.java` — implement `solve`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

**Hint:** validity formula: `(windowSize - maxFreq) <= k`

Say `hint` if stuck. Say `done` when finished.
