# Review — q069 Bet References Per Market

**Result: PASS** (implementation correct; test coverage partial)

---

## Implementation

Matches reference solution. All constraints satisfied:

- `Collectors.mapping` as `groupingBy` downstream ✓
- Sort inside `collectingAndThen` finisher = per-group, not global ✓
- Empty input → empty map (stream collector handles naturally) ✓

Finisher timing correct: runs once after full group accumulation, not per-element.

Only difference from `solution/Solution.java`: uses fully-qualified `Collectors.*` instead of static imports — minor style, not a correctness issue.

---

## Tests

| Required case | Status |
|---|---|
| Multiple bets, multiple markets — grouped + sorted per market | ✓ |
| Single-bet market → single-element list | ✗ missing |
| Empty input → empty map | ✗ missing |

Additional gap: each market has 3 refs but only indices 0 and 1 are asserted — index 2 never checked.

Missing tests to add:

```java
@Test
void should_return_single_element_list_for_single_bet_market() {
    List<MyAnswer.Bet> bets = List.of(
        bet("ref-1", "MKT-A", "bettor-1", BigDecimal.TEN)
    );
    assertEquals(List.of("ref-1"), MyAnswer.betRefsByMarket(bets).get("MKT-A"));
}

@Test
void should_return_empty_map_for_empty_input() {
    assertEquals(Map.of(), MyAnswer.betRefsByMarket(List.of()));
}
```

---

## Score

| Axis | Result |
|---|---|
| Correctness | PASS |
| Constraints (mapping + collectingAndThen) | PASS |
| Test coverage | Partial — 2 of 3 required cases missing |
