**Lessons:** [0001 Odds Feed Observer](../../../../../../../../topics/ood/observer/0001-odds-feed-observer.html)

# q047 — Live Odds Feed

**Category:** OOD · **Difficulty:** Medium · **Ideal time:** 20 min

---

## Problem

A sportsbook's live odds feed publishes odds changes for in-play events. Any number of
downstream systems need to react to every odds change — a price display board, a market
suspension monitor — without the feed itself knowing those systems exist. Use the
**Observer Pattern** so new reactions to odds changes can be added without ever touching the
feed.

---

## Requirements

1. `OddsChangedEvent` — an immutable record: `eventName` (String), `selectionName`
   (String), `previousOdds` (`BigDecimal`), `newOdds` (`BigDecimal`).
2. `OddsListener` — the **Observer Pattern** interface: `void onOddsChanged(OddsChangedEvent event)`.
3. `OddsFeed` (**Subject**): `addListener(OddsListener)`, `removeListener(OddsListener)`,
   `publishOddsChange(String eventName, String selectionName, BigDecimal newOdds)`.
   `OddsFeed` must not depend on any concrete listener class — only on `OddsListener`.
4. `publishOddsChange()` must update `OddsFeed`'s own internal record of current odds for
   that selection **before** notifying listeners — every listener must see the change as
   already applied, not as pending.
5. The listener collection must be safe for concurrent registration/removal while an event
   is being published (a listener could register/unregister itself, or another thread could,
   mid-publish).
6. `PriceDisplayListener` (concrete `OddsListener`): tracks the latest odds per
   `selectionName`, exposed via `getLatestOdds(String selectionName)`.
7. `MarketSuspensionListener` (concrete `OddsListener`): configurable percentage-move
   threshold via constructor. When a single odds change moves by that percentage or more
   (in either direction), the selection is marked suspended. Exposes
   `isSuspended(String selectionName)`. A move below the threshold must not suspend
   anything.
8. `BigDecimal` for every odds value and percentage — never `double`/`float`.

---

## Class diagram (skeleton guide)

```
OddsListener (interface)             ← Observer
  PriceDisplayListener
  MarketSuspensionListener

OddsChangedEvent (record)              (eventName, selectionName, BigDecimal previousOdds, BigDecimal newOdds)
OddsFeed                                ← Subject: holds List<OddsListener>, fires after updating own state
```

---

## Constraints

- Adding a new listener type must require zero changes to `OddsFeed`.
- Fire events **after** `OddsFeed`'s own state change — listeners must see the updated
  odds, not the stale value.
- `MarketSuspensionListener`'s threshold must be configurable via constructor, not
  hardcoded.
- No Spring, no persistence, pure Java 21.

---

## Folder layout

(Standard for `next ood` — see CLAUDE.md's "OOD Delivery Convention" section: full
solution-walkthrough lesson + complete `solution/` (including a runnable
`OddsFeedMain.java`) + empty `answer/`, mirrored under both `src/main` and `src/test`.)

```
q047_odds_feed/
├── question.md
├── solution/   ← full reference implementation, one class per file, TDD-ordered, includes OddsFeedMain.java
└── answer/     ← empty (.gitkeep) — write from scratch, or study the lesson + solution/ first
```

---

## Hints

If stuck → type `hint`
To see full solution → type `solution`
To submit → type `done`
