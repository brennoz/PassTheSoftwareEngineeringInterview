# Exercise — Concurrent Event Counter (Medium)

**Concepts:** `ConcurrentHashMap` atomic compute-family methods, check-then-act races
**Estimated time:** 25 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting)

---

## Problem

`MyAnswer` is an event counter. Multiple threads call `record(String event)` concurrently, for
possibly the same event string, at the same time. Implement `record()` so the final counts are
always correct — no lost updates — no matter how the calls interleave across threads.

```java
public void record(String event)   // increments this event's count by 1
public int countOf(String event)   // current count, 0 if never recorded
```

## Hard constraints (this is the actual point of the exercise)

- **No `synchronized`**, no explicit `Lock`/`ReentrantLock`. The field is already a
  `ConcurrentHashMap` — the exercise is to use ITS atomicity correctly, not bolt your own
  locking on top of a concurrent collection that already offers one.
- **No separate "read, then write" steps.** This is the actual bug the exercise is testing for:

```java
// WRONG — races under concurrent calls, even though it "looks" fine single-threaded:
Integer current = counts.get(event);          // thread A reads 3
// ... thread B also reads 3 here, before A writes back ...
counts.put(event, current + 1);                // both A and B write 4 — one increment LOST
```

  `get()` then `put()` is two separate operations on the map. Between them, another thread can
  run its own `get()`/`put()` pair against the same key, and one of the two increments silently
  disappears. This is check-then-act, the same bug shape this project's code-review rubric
  flags repeatedly for account-balance/overdraft scenarios — same root cause, different data
  structure.
- The whole increment — read current value (or absent), compute new value, write it back —
  must happen as ONE atomic operation as far as the map is concerned.

## Key questions before coding

- `ConcurrentHashMap` has several methods in the "atomic compute" family
  (`compute`, `computeIfAbsent`, `computeIfPresent`, `merge`). Which one most directly
  expresses "combine the existing value with 1, or use 1 if there was no existing value" in a
  single call?
- Why does making the FIELD a `ConcurrentHashMap` (instead of a `HashMap`) not, by itself,
  make `get()` followed by `put()` safe? What does `ConcurrentHashMap` actually guarantee
  about a single method call vs a sequence of two calls?

## TDD workflow — this one needs a real concurrency test

1. Write `MyAnswerTest.java` yourself. Include at least one single-threaded happy-path test
   AND one genuinely concurrent test: spin up an `ExecutorService` with several threads, have
   many of them call `record("same-event")` concurrently (e.g. 10 threads × 1,000 calls
   each), wait for all to finish (`ExecutorService.invokeAll` or a `CountDownLatch` +
   `awaitTermination`), then assert `countOf("same-event")` equals the exact expected total
   (10,000). A racy implementation will usually — not always, that's the nature of a race —
   fail this test with a lower count.
2. Red → fail (throws `UnsupportedOperationException`, or the concurrent test flakes/fails
   against a naive get-then-put implementation). Implement `record()`. Green → pass,
   consistently — run the concurrent test a few times, not just once, since a race that
   doesn't trigger on one run isn't proof it's fixed.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q051_concurrent_event_counter.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
