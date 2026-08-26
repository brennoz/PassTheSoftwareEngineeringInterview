# Review — q001 Two Sum

**Date:** 2026-05-21 | **Result:** ✅ PASS

## Approach used
HashMap storing `complement → index`. Single pass. Checks if current value exists as a previously stored complement.

## Rubric

| Criterion | Result | Note |
|---|---|---|
| Correctness | ✅ | All 3 examples correct |
| Time complexity | ✅ O(n) | Single pass, O(1) HashMap lookup |
| Space complexity | ✅ O(n) | HashMap at most n entries |
| Edge cases | ✅ | Duplicates [3,3] handled, negatives work |
| JUnit tests | ⚠️ | Only main() — write proper JUnit tests next time |
| Code hygiene | ⚠️ | Remove thinking-comment block before interview |

## Key insight to remember
Map stores `complement → index`. When you reach element `x`, check if `x` is already stored as a complement. If yes, its stored index + current index = answer.

Alternative (equally valid, slightly more readable):
```java
// store value → index, check target - nums[i]
if (map.containsKey(target - nums[i])) return new int[]{map.get(target - nums[i]), i};
map.put(nums[i], i);
```

## Gaps to fix before retry
- Write JUnit tests (happy path + duplicate values case)
