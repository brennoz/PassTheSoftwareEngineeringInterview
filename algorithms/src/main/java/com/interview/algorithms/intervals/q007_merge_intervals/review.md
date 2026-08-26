# Review — Q007 Merge Intervals

**Result: ✅ Pass**  
**Tests: 2/2**

---

## Correctness
Correct. Sort + single-pass scan. Overlap condition right (`current[0] > lastMerged[1]` for no overlap). `Math.max` on end handles fully-contained intervals correctly.

## Complexity
- Time: O(N log N) — sort dominates. Scan is O(N). ✅ Optimal.
- Space: O(N) — output list. ✅

## Edge Cases
- Touching intervals (`[1,4],[4,5]`) → covered by `>` not `>=`. ✅
- Fully contained (`[1,4],[2,3]`) → Math.max handles it. ✅
- Unsorted input → sort first. ✅

## Issues (minor style)

1. `main()` in solution class — remove before interview. Scratch testing belongs in `MyAnswerTest`.
2. Commented-out line 40 — dead code, delete it.
3. `int[] lastMerged = new int[] {0, 0}` — never used at that value. First iteration always hits `results.isEmpty()`. Use `null` or omit initial value.
4. Index loop where for-each suffices — no `i` needed, use `for (int[] current : intervals)`.
