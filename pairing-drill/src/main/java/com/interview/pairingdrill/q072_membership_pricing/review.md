# Review — q072 Customer Membership Pricing

**Category:** `pairing-drill` — no pass/fail, no difficulty tracker (see `CLAUDE.md`). Rubric
feedback only, same five axes as the real PairingCo-style pairing round.
**Full record:** `transcript.md` (4 passes, all test runs actually executed via `mvn test`, not
narrated)

---

## Reading unfamiliar code

Mixed. You didn't do the "say back what this does before touching it" step from the playbook —
went straight to "let's write a test" both in this drill and in the earlier chat rep. That's a
repeat pattern across two separate sessions, worth treating as a habit gap rather than a
one-off.

On the other hand, once inside the code you engaged with what you found rather than skimming
past it — you noticed the duplication between register/renew unprompted, and when told the
field was `private`, you actually tried reading it from the test yourself instead of taking it
on faith.

## Leading safely

Real strength here, consistent across both sessions: test-first before touching production
code, unprompted, before any hint. When the getter you added surfaced a genuine failing test
(the static-field leak between test methods), you didn't brush past the red — you engaged with
why it failed.

Weaker: once `solution` was given, the working files ended up matching it almost exactly rather
than you taking the idea and re-deriving the implementation. Then two further requests
("please complete the rest and show me", "please rewrite also the other test") had me writing
the code directly rather than you narrating it with me reacting. In the actual round, there is
no `solution` command and no one else can write the code for you — this is the single most
important thing to change in the time you have left before Wednesday.

## Technical communication

Good when you did it: the characterization-vs-regression distinction in the earlier rep, the
"maybe the field should be public" idea followed by actually engaging with the pushback rather
than just complying, are both real instances of reasoning out loud under challenge — that's
exactly what's graded.

But the through-line of this session is asking for the answer at the first sign of friction
("not sure how to test it", "solution please", "please complete the rest") rather than
narrating an attempt, even a wrong one, and adjusting from pushback. A wrong-but-narrated
attempt outscores silence-then-request-the-answer in the real room — there's no hint button
there.

## Balanced engineering

The final design — deleting the counter field entirely and deriving it from a repository
query, instead of just making it thread-safe in place — is the right level of fix, not over-
or under-engineered. Good instinct to actually implement it rather than leave it as a
suggestion. Can't fully credit this as your own judgment call this session, though, since it
arrived via `solution` rather than being derived under pushback the way the earlier rep's
`equalsIgnoreCase` vs enum tradeoff was.

## Prioritization

When three open items were listed at the end of pass 3 (idempotency key, charge-fails test,
dropped `save` verify), you took all three at once rather than picking the highest-impact one
and explicitly deferring the rest with a stated reason. Fine in an untimed drill; in a real
45–60 minute round, treating every flagged item as equally urgent is how the clock runs out
mid-change. Practice explicitly saying "I'm doing X now, parking Y and Z" even when you
technically have time to do all three — it's the narration that's graded, not just the outcome.

---

## What to drill before Wednesday, in priority order

1. **Type through the uncertain part yourself before asking for it.** This is the one pattern
   that repeated across both sessions. Next rep: when you hit "not sure how to do X," narrate a
   guess out loud and let it be wrong, rather than requesting the answer.
2. **Practice explicit deferral under time pressure** — say what you're not doing and why, even
   when nothing is forcing you to stop.
3. Keep doing what's already working: test-first, engaging with pushback rather than silently
   complying, and treating a real test failure as signal rather than noise.

`transcript.md` has the full pass-by-pass record if you want to re-read any exchange before the
next drill.
