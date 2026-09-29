# Pairing Drill — q076 Loan Disbursement (Medium)

**Category:** `pairing-drill` — mentoring/pairing refactor, roleplay always on
**Flavor:** Spring-specific (first drill in this flavor) — field injection vs constructor
injection, `@Transactional` boundary, missing optimistic lock, no timeout on an external call
**Domain:** Banking (first pairing-drill in this vertical)
**Pairing partner:** junior (default)
**Estimated time:** 45–60 min (mirrors a real live pairing round — finishing is not the goal)

**Folder shape:**
- `LoanDisbursementService.java` (this directory) — the **original, seeded code. Read-only
  reference. Do not edit this one.**
- `solution/` — the complete, fixed, heavily commented reference. Here from the start; the
  discipline of not peeking early is on you.
- `answer/` — **this is where you actually work.** Starts as an exact copy of the seeded code,
  including a small reflection-based test-setup helper — see the comment in
  `answer/MyAnswerTest.java` for why it's there and when you can delete it.

---

## Scene

Two engineers are pairing with you — **Junior A** and **Junior B**.

> "A loan got disbursed twice last week — same loan ID, two ledger entries, same amount. Best
> guess is a client retry, since the request timed out on our end first. We also spent way
> longer than expected just trying to write a test that reproduces it, because half these
> classes are painful to instantiate outside of Spring."

Two different problems bundled into that description again — one is the actual incident, one
is a complaint about testability. Worth deciding whether fixing one helps with the other.

## Starting code

`LoanDisbursementService.java` as seeded is deliberately messy — planted issues are not listed
here, and the count isn't confirmed until `done`. Read it yourself first, along with
`LoanAccount`, `LedgerEntry`, `LoanAccountRepository`, `LedgerRepository`, `CreditBureauClient`.

## How this drill runs

1. **Orient** — read the seeded code and its collaborators. Say back what it currently does
   before changing anything.
2. **Edit `answer/LoanDisbursementService.java` and `answer/MyAnswerTest.java` directly** in
   your editor — not in chat. The main-folder file and `solution/` stay as reference.
3. **`review`** — repeatable, non-terminal. Claude reads the current state of `answer/`, runs
   `mvn test -pl pairing-drill`, reacts in character, and appends the exchange + a diff summary
   to `transcript.md`. Call it as often as you want.
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
  -Dtest="com.interview.pairingdrill.q076_loan_disbursement.answer.MyAnswerTest"
```

---

*Say `review` any time you want feedback mid-flow. `done` when you want to close it out.*
