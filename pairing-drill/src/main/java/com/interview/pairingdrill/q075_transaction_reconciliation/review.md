# Review — q075 Transaction Reconciliation

**Category:** `pairing-drill` — no pass/fail, no difficulty tracker. Rubric feedback only.
**Full record:** `transcript.md` (3 passes, all test runs actually executed via `mvn test`)
**Compared against:** `q072`'s `review.md` (the prior session's growth areas) — noted below
where this session shows real movement.

---

## Reading unfamiliar code

Strongest showing of the three sessions on record. The standout moment: you noticed
`reconcileByCurrency` converts each amount by the exchange rate but keeps the map keyed by the
*original* currency — an internal inconsistency in the seeded code itself, not something I'd
flagged or hinted at. That's a genuinely sharper catch than either prior session produced —
spotting a semantic bug in the code's own design, not just a mechanical one.

Still skipped the "say back what this does" orientation step before touching anything — same
gap named after `q072`. Two for two now; worth deliberately forcing it next time even when
you're confident you already understand the code.

## Leading safely

Consistently strong. Test-first before any production-code edit, again, unprompted. Caught your
own arithmetic mistake by recomputing by hand rather than assuming the failure meant what it
first looked like. Deliberately isolated the audit fix from the rate fix ("removed the audit
bit for a sec") instead of changing everything at once. This is the pattern from `q072`'s one
genuine strength, now repeated reliably rather than as a single instance.

## Technical communication

This is where the real movement from `q072`'s feedback shows. That review's headline finding
was: type through the uncertain part yourself before asking for it. This session, you asked for
exactly one hint at the very start, then drove the rate-provider interface decision, the
`Collectors` rewrite, the audit reconnection, and the self-containment fix entirely yourself —
no more "please complete the rest" pattern.

One gap remains, different in kind from before: narration sometimes lagged the edit rather than
preceding it. `auditAsync` was reconnected between messages without being said out loud first —
I only found it by re-reading the file, and you separately asked me to verify it was unused
after already having changed that. In the real round, say what you're about to do *before* you
do it, not after, even for a quick change.

## Balanced engineering

The interface-level fix on `RateProvider` (`Optional<BigDecimal>` return type, not just a local
`Optional.ofNullable` wrap) was your own reasoned call, articulated clearly: "more correct to go
for the interface root." That's exactly the kind of tradeoff-aware decision this axis rewards —
and it was a genuine decision between two real options, not the only path available. The
`Collectors.groupingBy` rewrite is scoped to exactly what needed fixing. Dead commented-out code
was removed once flagged rather than left "just in case."

## Prioritization

Real, demonstrated skill here — the other named growth area from `q072`. You explicitly parked
the currency-grouping ambiguity with a stated reason and went after the deterministic
null-rate bug first, then worked through the remaining bugs in a sensible dependency order.
That's the "explicit deferral" habit `q072`'s review asked you to practice, showing up
unprompted this time.

One refinement for next time: deferring cleanly is good, but the currency-grouping question
was never actually resolved — it's still open at the bottom of this file. In a real timed
round, a deferred item should get flagged for resolution before wrap-up ("here's the one thing
I never got an answer to"), not just left parked indefinitely.

---

## What changed since `q072`, concretely

| `q072` finding | This session |
|---|---|
| Asked for `hint`/`solution`/"complete the rest" repeatedly | One hint only, drove the rest yourself |
| Didn't practice explicit deferral | Deferred the ambiguity with a stated reason, unprompted |
| — | New gap: narrate before editing, not after |
| — | New gap: a deferred item still needs closing out, not just parking |

## Still open at close

The currency-grouping semantic ambiguity (convert-then-group-by-original-currency vs. some
other intended reading) was never resolved — flagged three times across the session, never
revisited. Worth actually deciding next time you're in a similar spot, rather than letting a
parked item stay parked through to `done`.

`transcript.md` has the full pass-by-pass record.
