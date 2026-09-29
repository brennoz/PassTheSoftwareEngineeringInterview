# Pairing Drill — q073 Order Fulfillment (Medium)

**Category:** `pairing-drill` — mentoring/pairing refactor, roleplay always on
**Estimated time:** 45–60 min (mirrors a real live pairing round — finishing is not the goal)
**Your file:** `OrderFulfillmentService.java` (this directory) — edit in place, do not rewrite
from scratch
**Tests:** `MyAnswerTest.java` (mirrored `src/test` path) — starts empty, mocks pre-wired,
write your own
**Transcript:** `transcript.md` (this directory) — appended automatically on every `review`

---

## Scene

Two engineers are pairing with you — **Junior A** and **Junior B**. Treat them as junior
developers you are mentoring through the change, not an audience.

> "We had a support ticket last week — a customer's order got marked dispatched but the
> warehouse says the stock was never actually reduced. We think it's in here somewhere. Also
> this is the code path both dispatch and re-dispatch (after a failed delivery) go through, and
> it's getting hard to change one without breaking the other."

There's a specific reported symptom this time (unlike `q072`, which was a pure structural
ask) — but it's vague ("something's wrong with stock"), not a precise known-wrong expected
value. Decide for yourself what kind of safety-net test that calls for before you start.

## Starting code

`OrderFulfillmentService.java` as seeded is deliberately messy — planted issues are not listed
here, and the count isn't confirmed until `done`. Read it yourself first, along with `Order`,
`OrderItem`, `Customer`, `Address`, `InventoryRecord`, `OrderRepository`,
`InventoryRepository`, `ShippingNotifier`.

## How this drill runs

1. **Orient** — read the seeded code and its collaborators. Say back what it currently does
   before changing anything.
2. **Edit `OrderFulfillmentService.java` and `MyAnswerTest.java` directly** in your editor —
   not in chat.
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
  -Dtest="com.interview.pairingdrill.q073_order_fulfillment.MyAnswerTest"
```

---

*Say `review` any time you want feedback mid-flow. `done` when you want to close it out.*
