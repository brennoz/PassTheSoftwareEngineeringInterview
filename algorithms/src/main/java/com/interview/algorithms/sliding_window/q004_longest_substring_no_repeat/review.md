# Review — Q004 Longest Substring Without Repeating Characters

**Result: PASS**

---

## Correctness ✅
All 3 examples produce correct output. Unified sliding window is the canonical solution.

## Time Complexity ✅ O(N)
Each char enters and leaves `sw` at most once. Two pointers never go backward.

## Space Complexity ✅ O(min(N, charset))
HashSet bounded by alphabet size (128 ASCII / 65536 Unicode) — effectively O(1) for practical inputs.

## Edge Cases ✅
- `""` → 0 (loop never runs)
- `" "` → 1 (space is valid char)
- `"au"` → 2

## Code Quality ✅
- `r - l + 1` instead of `sw.size()` — O(1) vs O(N), correct call
- Single loop, no if/else branching — clean

## Missing ⚠️
No `MyAnswerTest.java`. Should have 2–4 tests covering:
- Happy path: `"abcabcbb"` → 3
- All-same: `"bbbbb"` → 1
- Empty string: `""` → 0
- Single char: `" "` → 1
