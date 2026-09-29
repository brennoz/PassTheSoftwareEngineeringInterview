# Pairing Drill — q077 Market Settlement (Medium)

**Category:** `pairing-drill` — mentoring/pairing refactor, roleplay always on
**Flavor:** Type safety / primitive obsession (first drill in this flavor)
**Domain:** Sport betting
**Pairing partner:** junior (default)
**Estimated time:** 45–60 min

**Folder shape:**
- `MarketSettlementService.java` (this directory) — **original seeded code. Read-only reference. Do not edit this one.**
- `solution/` — complete, fixed, heavily commented reference. Here from the start.
- `answer/` — **this is where you actually work.** Starts as an exact copy of the seeded code.

---

## Scene

Two junior engineers are pairing with you — **Junior A** and **Junior B**.

> "We had a bad incident last week. A market was settled with zero winners — every winning
> bet was marked as lost. Best guess after the post-mortem: someone called `settleMarket`
> with `"home"` (lowercase) instead of `"HOME"`. The String comparison silently matched
> nothing, so no bets were marked won and the payout went out as zero. Customers are angry."
>
> "There's also a separate thing — the product team keeps asking for new market states
> (`VOID`, `NO_RESULT`) and every time we add one, we have to grep the whole codebase for
> hardcoded strings. It took three PRs last sprint to add one status."

Two different problems bundled in that description. One caused last week's incident. One is
a maintainability complaint. Worth deciding whether the same fix addresses both.

## Starting code

`MarketSettlementService.java` as seeded is deliberately messy. Planted issues are not
listed here, and the count isn't confirmed until `done`. Read it yourself first, along with
`Market`, `Bet`, `Stake`, `MarketRepository`, `BetRepository`.

## How this drill runs

1. **Orient** — read the seeded code and its collaborators. Say back what it currently does
   before changing anything.
2. **Edit `answer/MarketSettlementService.java`** and the supporting files in `answer/` directly
   in your editor. The main-folder files and `solution/` stay as reference.
3. **`review`** — repeatable, non-terminal. Claude reads the current state of `answer/`, runs
   `mvn test -pl pairing-drill`, reacts in character, appends exchange + diff to `transcript.md`.
4. **`hint`** — nudge without breaking character.
5. **`solution`** — points to the pre-written `solution/` and walks through it.
6. **`done`** — terminal. Writes `review.md` against the pairing rubric.

## Run your tests directly

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl pairing-drill \
  -Dtest="com.interview.pairingdrill.q077_market_settlement.answer.MyAnswerTest"
```

---

*Say `review` any time you want feedback mid-flow. `done` when you want to close it out.*
