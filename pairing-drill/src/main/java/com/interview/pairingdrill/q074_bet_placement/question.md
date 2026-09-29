# Pairing Drill — q074 Bet Placement (Medium-Hard)

**Category:** `pairing-drill` — mentoring/pairing refactor, roleplay always on
**Flavor:** OOD structural + concurrency/synchronization (see "Pairing Drill Category" in
`CLAUDE.md` for the full flavor rotation)
**Estimated time:** 45–60 min (mirrors a real live pairing round — finishing is not the goal)
**Your file:** `BetPlacementService.java` (this directory) — edit in place, do not rewrite from
scratch
**Tests:** `MyAnswerTest.java` (mirrored `src/test` path) — starts empty, mocks pre-wired, write
your own
**Transcript:** `transcript.md` (this directory) — appended automatically on every `review`

---

## Scene

Two engineers are pairing with you — **Junior A** and **Junior B**. Treat them as junior
developers you are mentoring through the change, not an audience.

> "We're about to add a new market status — 'DELAYED', for when an event's start time slips —
> and every time someone touches this method to add a status we're scared of breaking one of
> the existing ones. Also, trading flagged something: liability on a couple of markets went
> over our max during a busy Saturday, and we can't figure out how, since the check is right
> there in the code."

Two things going on, deliberately — one is about the shape of the code, one is about what
happens when two requests hit it at the same time. Decide for yourself whether they're actually
separate problems or connected.

## Starting code

`BetPlacementService.java` as seeded is deliberately messy — planted issues are not listed
here, and the count isn't confirmed until `done`. Read it yourself first, along with `Market`,
`Bet`, `BetResult`, `BetRepository`.

## How this drill runs

1. **Orient** — read the seeded code and its collaborators. Say back what it currently does
   before changing anything.
2. **Edit `BetPlacementService.java` and `MyAnswerTest.java` directly** in your editor — not in
   chat.
3. **`review`** — repeatable, non-terminal. Claude reads your current files, runs
   `mvn test -pl pairing-drill`, reacts in character as Junior A / Junior B, and appends the
   exchange + a diff summary to `transcript.md`. Call it as often as you want.
4. **`hint`** — stuck, need a nudge, without breaking character permanently.
5. **`solution`** — full walkthrough, breaks character to explain directly.
6. **`done`** — terminal. Writes `review.md` against the pairing rubric (technical
   communication, balanced engineering, prioritization, reading unfamiliar code, leading
   safely) and closes the session.

## Run your tests directly (optional — `review` also does this for you)

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl pairing-drill \
  -Dtest="com.interview.pairingdrill.q074_bet_placement.MyAnswerTest"
```

---

*Say `review` any time you want feedback mid-flow. `done` when you want to close it out.*
