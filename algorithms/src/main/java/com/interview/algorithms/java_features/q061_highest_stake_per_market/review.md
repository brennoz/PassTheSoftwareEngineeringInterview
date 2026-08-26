# Review — q061 highest_stake_per_market

**Result: ✅ Pass**

`Collectors.toMap(Bet::marketId, Function.identity(), BinaryOperator.maxBy(Comparator.comparing(Bet::stake)))`
— matches the reference exactly. All three hard constraints met: 3-arg `toMap`, merge function
is genuinely value-based (not first/last-wins), `BigDecimal` throughout.

Test: one `@Test`, but well-constructed — `mk-2` (30, 70, 10) and `mk-3` (50, 51, 20) both place
the winning stake in the middle of the list, which specifically rules out a "keep first
argument" or "keep second argument" merge-function bug that a winner-at-either-end dataset
would let slip through. `mk-1` covers the plain 2-bet case. `BigDecimal.compareTo`, not
`equals`, used for the assertions — correct per project convention.

Not covered, minor: a single-bet market, and an explicit reversed-order variant of the same
data (not load-bearing here since the middle-position winners already prove order-independence
indirectly, but would be the more direct way to state that intent).

No fix needed.
