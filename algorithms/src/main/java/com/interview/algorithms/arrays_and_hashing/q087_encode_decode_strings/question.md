# q087 — Encode and Decode Strings

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 20 min

---

## Problem

Design an algorithm to encode a list of strings to a single string. The encoded string is then sent over the network and decoded back to the original list of strings.

Implement `encode` and `decode` methods — there is no restriction on what the encoded string can look like.

---

## Examples

```
Input:  ["neet","code","love","you"]
Encoded: "4#neet4#code4#love3#you"
Decoded: ["neet","code","love","you"]

Input:  ["we","say",":","yes"]
Encoded: "2#we3#say1#:3#yes"
Decoded: ["we","say",":","yes"]
```

---

## Constraints

- 0 <= strs.length < 100
- 0 <= strs[i].length < 200
- strs[i] contains any UTF-8 character

## Recommended complexity

O(m) time for encode and decode, O(m + n) space — where m = total chars, n = number of strings.

---

## Your task

Edit `MyAnswer.java` — implement `encode` and `decode`.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
