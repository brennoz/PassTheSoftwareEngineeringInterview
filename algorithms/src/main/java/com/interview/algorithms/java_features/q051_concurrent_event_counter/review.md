# q051 Review — Concurrent Event Counter

**Result: PASS — 3/3 tests, verified stable across 3 separate runs (9 executions, 0 failures)**

---

## What You Got Right

- `counts.merge(event, 1, Integer::sum)` — single atomic call, matches the reference solution
  exactly. No `synchronized`, no external lock.
- Genuinely empirically tested the failure mode yourself before settling on the fix — your
  commented-out lines show you tried a plain `HashMap` (confirmed it fails) and a
  `ReentrantLock` (confirmed it's unnecessary once `merge()` is used) before landing on the
  atomic-only version. That's exactly the kind of verification this exercise was designed to
  provoke, not something I asked for — good instinct.
- Concurrency test is real, not decorative: 10 threads × 1,000 calls to the same key via a
  proper `ExecutorService` + `awaitTermination`, asserting the exact expected total. Ran it
  3 times against your implementation myself — 3/3 clean every time.
- `unrecordedEventReturnsZero` covers the `getOrDefault` path independently of the increment
  logic.

## One nuance worth knowing (not a deduction — your code is correct)

You typed the field as `Map<String, Integer>` (interface) but constructed a
`ConcurrentHashMap`. That's fine here — `merge()` dispatches virtually to
`ConcurrentHashMap`'s own atomic override at runtime, regardless of the reference type it's
called through. But it's worth knowing WHY that's fine: `Map.merge()` has a *default*
implementation on the interface that is **not** atomic (it's literally get-then-put under the
hood, the same race this exercise is about) — `ConcurrentHashMap` overrides it specifically to
make it atomic. If the field had been constructed as a plain `HashMap` instead (which your own
commented-out experiment already showed fails), calling `.merge()` on it would silently run the
non-atomic default. The atomicity guarantee lives in the concrete class, not the interface
contract — good to be able to say that out loud if asked why `Map`-typed code is still safe
here.

## Summary

First medium-difficulty `java` question, clean pass. Understood the race, chose the correct
atomic primitive, and — notably — verified the failure mode empirically rather than taking it
on faith.

---

**Difficulty progression:** 1st pass at `java` medium. One more clean pass at medium → bumps
to hard for the next `next java`.
