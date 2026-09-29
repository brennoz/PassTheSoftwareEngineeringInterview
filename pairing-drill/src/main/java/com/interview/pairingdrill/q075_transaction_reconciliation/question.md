# Pairing Drill — q075 Transaction Reconciliation (Medium)

**Category:** `pairing-drill` — mentoring/pairing refactor, roleplay always on
**Flavor:** Java SDK / functional idioms (streams, `Optional`, `CompletableFuture`,
`Collectors`) — first drill built under the new three-way folder shape, see "Pairing Drill
Category" in `CLAUDE.md`
**Pairing partner:** junior (default)
**Estimated time:** 45–60 min (mirrors a real live pairing round — finishing is not the goal)

**Folder shape (new — read this once, it's different from q072–q074):**
- `TransactionReconciliationService.java` (this directory) — the **original, seeded code.
  Read-only reference. Do not edit this one.**
- `solution/` — the complete, fixed, heavily commented reference. It's here from the start;
  nothing stops you opening it early, but the discipline of not peeking is on you.
- `answer/` — **this is where you actually work.** Starts as an exact copy of the seeded code.
  Edit `answer/TransactionReconciliationService.java` and write your tests in
  `answer/MyAnswerTest.java` (mirrored under `src/test`).

---

## Scene

Two engineers are pairing with you — **Junior A** and **Junior B**. Treat them as junior
developers you are mentoring through the change, not an audience.

> "The nightly reconciliation job crashes intermittently in prod — sometimes it throws right
> away, sometimes it just hangs for a while, and once or twice finance said an audit log entry
> was just... missing, with nothing in the error logs at all. We can't reproduce it reliably
> locally, which is why we haven't been able to fix it ourselves."

That description is doing a lot of work — three different symptoms, described as if they might
be one bug or several. Decide for yourself whether they're related before you start changing
anything.

## Starting code

`TransactionReconciliationService.java` as seeded is deliberately messy — planted issues are
not listed here, and the count isn't confirmed until `done`. Read it yourself first, along with
`Transaction`, `RateProvider`, `ExternalAuditService`, `AuditException`.

## How this drill runs

1. **Orient** — read the seeded code and its collaborators. Say back what it currently does
   before changing anything.
2. **Edit `answer/TransactionReconciliationService.java` and `answer/MyAnswerTest.java`
   directly** in your editor — not in chat. The main-folder file and `solution/` stay as
   reference; nothing you do should modify either of them.
3. **`review`** — repeatable, non-terminal. Claude reads the current state of `answer/`, runs
   `mvn test -pl pairing-drill`, reacts in character as Junior A / Junior B, and appends the
   exchange + a diff summary to `transcript.md`. Call it as often as you want.
4. **`hint`** — stuck, need a nudge, without breaking character permanently.
5. **`solution`** — points you at the pre-written `solution/` and walks through it, breaking
   character to explain directly.
6. **`done`** — terminal. Writes `review.md` against the pairing rubric (technical
   communication, balanced engineering, prioritization, reading unfamiliar code, leading
   safely) and closes the session.

## Run your tests directly (optional — `review` also does this for you)

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl pairing-drill \
  -Dtest="com.interview.pairingdrill.q075_transaction_reconciliation.answer.MyAnswerTest"
```

---

*Say `review` any time you want feedback mid-flow. `done` when you want to close it out.*
