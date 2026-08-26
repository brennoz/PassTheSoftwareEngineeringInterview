# Review — q064 suspended_markets_summary

**Result: ❌ Fail**

## Bug — inverted filter

```java
.filter(market -> !market.status().equals(MarketStatus.SUSPENDED))
```

The `!` keeps every market that is NOT suspended — ACTIVE and CLOSED pass through, SUSPENDED
is the one status excluded. Spec asks for the opposite: a summary of exactly the suspended
markets.

Not an isolated typo — the test is built around the same inversion. The result variable is
named `notSuspendedMarkets`, and the assertion expects
`[match_101, match_102, match_202, match_203, match_301, match_302]` — the six ACTIVE/CLOSED
markets from the fixture — while `match_201` and `match_303` (the two actually `SUSPENDED`
markets) are the ones missing from that list. Implementation and test agree with each other,
both backwards against "suspended markets summary."

## What passed

- `Collectors.joining(", ", "[", "]")` — correct 3-arg form, correct bracket wrapping.
- `sorted(Comparator.naturalOrder())` before joining — deterministic output.
- `Market::marketId` mapping and the overall pipeline shape are otherwise correct.

## Fix

```java
.filter(market -> market.status().equals(MarketStatus.SUSPENDED))
```

or equivalently `market.status() == MarketStatus.SUSPENDED` (enum constants are singletons,
`==` is safe and more idiomatic here than `.equals()`).

## Retry

Reread the method name against what the filter keeps, not just what it excludes — "summary of
suspended markets" means the predicate should be true FOR a suspended market, not true for
everything else.
