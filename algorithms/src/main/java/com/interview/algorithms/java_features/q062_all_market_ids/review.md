# Review — q062 all_market_ids

**Result: ✅ Pass**

`slips.stream().flatMap(slip -> slip.selections().stream()).map(selection -> selection.marketId()).distinct().sorted().toList()`
— matches the reference exactly. `flatMap` correctly used to go from `Stream<BetSlip>` to a
flat `Stream<Selection>` in one step; `distinct()` runs on the flattened stream, not per-slip.

Test: one well-constructed case — `mk-1` repeated within `betSlip` (via two identical
`Selection("mk-1", "Roma")` entries) and `mk-1`/`mk-2` repeated again across `betSlip2`,
genuinely exercising cross-slip dedup rather than only within-slip. Assertions check `size()`
plus the first and a middle element (`mk-4` at index 3) rather than the full list — a
reasonable spot-check, though asserting the complete `List.of("mk-1", ..., "mk-5")` would have
been marginally stronger (would also catch a wrong-position bug at any of the untested indices).

No fix needed.
