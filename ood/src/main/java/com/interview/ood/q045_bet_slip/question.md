**Lessons:** [0001 Bet Slip Composite](../../../../../../../../topics/ood/composite/0001-bet-slip-composite.html)

# q045 — Bet Slip

**Category:** OOD · **Difficulty:** Medium · **Ideal time:** 25 min

---

## Problem

Design a bet slip system for a sportsbook. A customer can place a **single bet** (one
selection, one stake) or an **accumulator/parlay bet** (several selections combined into one
stake, all must win). A customer's slip can contain any mix of singles and accumulators, and
the sportsbook needs one uniform way to total up stake and payout across the whole slip —
without an `instanceof`/switch on bet type anywhere.

---

## Requirements

1. `Selection` — a value object: `eventName` (String), `odds` (`BigDecimal`, decimal odds
   e.g. `2.50`), `result` (`SelectionResult`: `PENDING` / `WON` / `LOST`). Starts `PENDING`;
   `settle(SelectionResult)` updates it.
2. `Wager` — the **Composite Pattern** component interface every bet-shaped node
   implements: `BigDecimal getStake()`, `BigDecimal calculatePayout()`, `boolean isWon()`.
3. `SingleBet` (**Leaf**) implements `Wager`: one `Selection` + one stake.
   `calculatePayout()` returns `stake * odds` only if the selection is `WON`, otherwise
   `ZERO`.
4. `AccumulatorBet` implements `Wager`: holds `List<Selection>` legs (**not** `List<Wager>`
   — legs are picks, not independently-staked sub-bets) + one stake. `isWon()` is true only
   when there is at least one leg and every leg is `WON`. `calculatePayout()` multiplies all
   legs' odds together and multiplies by stake, but only when `isWon()` — any `LOST` or still
   `PENDING` leg means zero payout.
5. `BetSlip` implements `Wager` (the actual recursive **Composite**): holds `List<Wager>`
   children, which may be `SingleBet`, `AccumulatorBet`, **or another `BetSlip`**.
   `getStake()` and `calculatePayout()` sum across all children uniformly; `isWon()` is true
   only when there is at least one child and every child is won.
6. `BigDecimal` for every monetary/odds value — never `double`/`float`.

---

## Class diagram (skeleton guide)

```
Wager (interface)             ← Composite component
  SingleBet                   ← Leaf (one Selection + stake)
  AccumulatorBet               ← composes List<Selection>, NOT List<Wager>
  BetSlip                      ← composes List<Wager> — the recursive node, can nest BetSlip

Selection                      (eventName, BigDecimal odds, SelectionResult result)
SelectionResult (enum)          PENDING / WON / LOST
```

---

## Constraints

- `AccumulatorBet` must NOT be constructible with `Wager` legs — legs are `Selection`
  only. This is a type-level decision, not a runtime check: don't add an `instanceof`
  guard where the compiler can already make the mistake impossible.
- `BetSlip` MUST support nesting another `BetSlip` as a child, and `calculatePayout()` /
  `isWon()` must recurse correctly through however many levels are nested.
- No Spring, no persistence, pure Java 21.

---

## Folder layout

(Standard for `next ood` — see CLAUDE.md's "OOD Delivery Convention" section: full
solution-walkthrough lesson + complete `solution/` (including a runnable
`BetSlipMain.java`) + empty `answer/`, mirrored under both `src/main` and `src/test`.)

```
q045_bet_slip/
├── question.md
├── solution/   ← full reference implementation, one class per file, TDD-ordered, includes BetSlipMain.java
└── answer/     ← empty (.gitkeep) — write from scratch, or study the lesson + solution/ first
```

---

## Hints

If stuck → type `hint`
To see full solution → type `solution`
To submit → type `done`
