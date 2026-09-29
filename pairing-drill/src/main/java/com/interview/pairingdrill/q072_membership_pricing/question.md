# Pairing Drill — q072 Customer Membership Pricing (Medium)

**Category:** `pairing-drill` — mentoring/pairing refactor, roleplay always on
**Estimated time:** 45–60 min (mirrors a real live pairing round — finishing is not the goal)
**Your file:** `CustomerMembershipService.java` (this directory) — edit in place, do not rewrite from scratch
**Tests:** `MyAnswerTest.java` (mirrored `src/test` path) — starts empty, mocks pre-wired, write your own
**Transcript:** `transcript.md` (this directory) — appended automatically on every `review`

---

## Scene

Two engineers are pairing with you — **Junior A** and **Junior B**. Treat them as junior
developers you are mentoring through the change, not an audience. The framing, verbatim from
what they've told you:

> "This is the one — every time we add a tier we touch it in two places and someone forgets
> the second one. We're about to add a 4th tier and want a second pair of eyes before we do."

No specific bug has been reported. This is a **pure structural refactor** ask — the code works,
it's just painful to extend. That distinction matters for what kind of safety-net test is
correct here (see Lesson 2 in `topics/projects/pairingco/` if you have it — characterization test,
not a regression/bug-reproduction test, since there's no known-wrong expected value to pin).

## Starting code

`CustomerMembershipService.java` as seeded is deliberately messy — planted issues are not
listed here, and the count isn't confirmed until `done`. Read it yourself first.

## How this drill runs

1. **Orient** — read the seeded code, `Customer`/`BillingAccount`/`CustomerRepository`/
   `PaymentGateway`/`NotificationClient` alongside it. Say back what it currently does before
   changing anything.
2. **Edit `CustomerMembershipService.java` and `MyAnswerTest.java` directly** in your editor —
   not in chat. Write tests, extract methods, rename, whatever the loop calls for.
3. **`review`** — repeatable, non-terminal. Claude reads your current files, runs
   `mvn test -pl pairing-drill`, reacts in character as Junior A / Junior B (pushback, naive
   questions, time-pressure checks), and appends the exchange + a diff summary to
   `transcript.md`. Call it as often as you want.
4. **`hint`** — stuck, need a nudge, without breaking character permanently.
5. **`solution`** — full walkthrough, breaks character to explain directly.
6. **`done`** — terminal. Writes `review.md` against the pairing rubric (technical
   communication, balanced engineering, prioritization, reading unfamiliar code, leading
   safely) and closes the session. `transcript.md` stays as the full record.

## Run your tests directly (optional — `review` also does this for you)

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl pairing-drill \
  -Dtest="com.interview.pairingdrill.q072_membership_pricing.MyAnswerTest"
```

---

*Say `review` any time you want feedback mid-flow. `done` when you want to close it out.*
