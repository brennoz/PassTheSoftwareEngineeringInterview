# q122 — Implement Trie (Prefix Tree)

**Category:** Algorithms | **Difficulty:** Medium | **Ideal time:** 25 min

---

## Problem

Implement a trie data structure supporting:

1. `insert(word)` — add a word to the trie
2. `search(word)` — return `true` if the exact word was inserted
3. `startsWith(prefix)` — return `true` if any inserted word starts with prefix
4. `findAllWithPrefix(prefix)` — return all words that start with prefix (autocomplete)

---

## Complexity

| Operation | Time | Space |
|---|---|---|
| insert | O(m) | O(m) |
| search | O(m) | O(1) |
| startsWith | O(m) | O(1) |
| findAllWithPrefix | O(m + k·avg_len) | O(k) |

Where m = word/prefix length, k = number of matching words.

---

## Your task

Edit `MyAnswer.java` — implement the `Trie` class with the `TrieNode` inner class provided as a skeleton.
Write tests in `MyAnswerTest.java` (TDD encouraged).

Say `hint` if stuck. Say `done` when finished.
