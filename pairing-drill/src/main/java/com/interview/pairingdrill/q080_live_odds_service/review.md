# q080 — Review

**Result: ✅ Pass**
**Flavor:** Concurrency / synchronization · **Domain:** Sport Betting · **Seniority:** Junior

---

## Issues found and fixed

| # | Issue | Found | Fixed | Tested |
|---|---|---|---|---|
| 1 | `HashMap` → `ConcurrentHashMap` | ✅ | ✅ | ✅ (concurrency test) |
| 2 | `HashSet` → `ConcurrentHashMap.newKeySet()` | ✅ | ✅ | ✅ (suspension test) |
| 3 | `long updateCount++` → `AtomicLong` | ✅ | ✅ | ✅ (100-thread count test) |
| 4 | Check-then-act race → `synchronized` | ✅ | ✅ | ✅ (block + reopen tests) |
| 5 | `double` → `BigDecimal` for odds | ✅ | ✅ | ✅ (1.65 precision test) |
| 6 | `String` → `MarketStatus` enum | ✅ | ✅ | ✅ (ArgumentCaptor test) |

6/6 issues found and fixed independently. No hints used.

---

## Rubric

### Reading unfamiliar code
Strong. Navigated a concurrent service with multiple interacting bugs — suspension logic,
collection types, and a counter — without needing the code walked through. Fixed all four
concurrency issues in a single pass rather than piecemeal.

### Technical communication
Good throughout. Explained each fix clearly in code structure (synchronized, ConcurrentHashMap,
AtomicLong) and the pairing partners were kept oriented at each step. Minor gap: the check-then-act
race — the most subtle issue — was fixed correctly but the explanation of *why* `synchronized` on
both methods is necessary (not just one) could be more explicit when pairing with juniors. Emma and
Noah would likely still not understand why fixing `acceptUpdate` alone isn't sufficient without that
extra sentence.

### Prioritisation
Good instinct to start with `BigDecimal` (financial correctness) before the concurrency issues.
In a real incident (bets accepted on suspended markets) the check-then-act race is the higher
priority — but both are valid starting points, and all issues were addressed in the same session.

### Balanced engineering
All fixes are proportionate. `synchronized` is the right call here (not ReentrantLock, not
Semaphore) — simpler, sufficient, and the intent is obvious. ConcurrentHashMap.newKeySet() is
idiomatic rather than over-engineered.

### Leading safely
Pairing partners were acknowledged and the fixes were reasoned through rather than just typed.
Kept the session focused and didn't over-explain.

---

## Gaps / carry-forward

1. **`getUpdateCount()` returns `AtomicLong`** — exposes the internal counter; callers could call
   `.set()` or `.getAndSet()` from outside. Should return `long` via `updateCount.get()`. The test
   works around this with `.longValue()` but the API is still leaky.

2. **`System.out.println` not removed** — the seeded log line in `acceptUpdate` was identified
   early but not deleted. In a betting platform this would log every incoming odds update to stdout —
   catastrophic noise at in-play traffic volumes (thousands per second during a match).

3. **Concurrency test covers `updateCount` but not the check-then-act race** — `should_count_updates_atomically_under_concurrency` proves `AtomicLong` works, but there is no multi-threaded test that fires `acceptUpdate` and `suspendMarket` concurrently and asserts that once suspended, no odds write reaches the repository. That race is the original incident trigger and the hardest bug in the class — ideally it has a dedicated concurrency test, not just a single-threaded `should_block_update_on_suspended_market`.

---

## AWS / distributed impact (for reference)

`synchronized` solves the race within a single JVM. On a multi-pod deployment (ECS, Kubernetes)
each pod has its own lock — the race is back. Real fix at scale: a distributed lock (Redis `SET NX PX`)
around the suspension check, or moving suspension state to Redis so `acceptUpdate` can do an atomic
`if (redis.sismember("suspended", marketId)) return` — single source of truth across all pods.
`AtomicLong` for `updateCount` has the same cross-pod blindspot: each pod counts its own updates.
Redis `INCR` gives a single atomic counter visible cluster-wide.
