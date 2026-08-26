# Exercise — Concurrent Fetch with Virtual Threads (Medium)

**Concepts:** Java 21 virtual threads (`Executors.newVirtualThreadPerTaskExecutor()`),
`ExecutorService` as `AutoCloseable`, `Future`, order-preserving concurrent collection
**Estimated time:** 30 min
**Your file:** `MyAnswer.java` (this directory)
**Reference:** `solution/Solution.java` (open only after attempting) — also see
`solution/ImperativeApproach.java`

---

## Problem

Implement, in `MyAnswer.java`:

```java
public static List<String> fetchAllConcurrently(List<String> customerIds, Function<String, String> fetchCustomerData) throws InterruptedException, ExecutionException
```

For each ID in `customerIds`, call `fetchCustomerData.apply(id)` — model this as a slow,
blocking I/O call (the tests will make it actually sleep). Run all of them **concurrently**,
not one after another, and return the results **in the same order as the input list**,
regardless of which one actually finishes first.

## Hard constraints (this is the actual point of the exercise)

- Use **virtual threads** specifically —
  `Executors.newVirtualThreadPerTaskExecutor()` — not a fixed-size platform-thread pool. A
  test checks `Thread.currentThread().isVirtual()` from inside the fetch calls.
- The `ExecutorService` must be created in a **try-with-resources** statement.
  `ExecutorService` has implemented `AutoCloseable` since Java 19 — `close()` initiates an
  orderly shutdown and waits for in-flight tasks, which is exactly what you want here instead
  of manually calling `shutdown()` and hoping nothing above you forgets to.
- Results must come back in the SAME order `customerIds` was given, even though the fetches
  complete out of order.

## Key questions before coding

- If you submit N tasks to an `ExecutorService` and get back a `List<Future<String>>` in
  submission order, does calling `.get()` on each `Future` in THAT list order give you results
  in submission order — even if task #5 finishes before task #1? What does `Future.get()`
  actually do if the task it refers to hasn't completed yet?
- Why might someone reach for `Executors.newFixedThreadPool(N)` here out of habit, and what's
  actually different about a virtual thread compared to a platform thread that makes
  "one virtual thread per task, however many tasks there are" a reasonable thing to do, where
  "one platform thread per task" usually isn't at any real scale?

## TDD workflow

1. Write `MyAnswerTest.java` yourself. Cover: results come back in input order even when a
   fetcher is rigged to make later items finish first (e.g. sleep time inversely related to
   position); every fetch call happens on a virtual thread (`Thread.currentThread().isVirtual()`,
   recorded into a thread-safe collection during the fetch calls); and the calls genuinely run
   concurrently, not sequentially — assert the wall-clock time for a batch of fetches (each
   sleeping some fixed duration) is well under what sequential execution would take.
2. Red → fail. Implement `fetchAllConcurrently()`. Green → pass.

## After you're green — compare against the imperative version

Read `solution/ImperativeApproach.java` — a plain sequential `for` loop, no concurrency at
all. Time both against the same input in `solution/Main.java` and look at the actual numbers.

## Run your tests

```bash
cd PassTheSoftwareEngineeringInterview
mvn test -pl algorithms \
  -Dtest="com.interview.algorithms.java_features.q058_virtual_thread_fetch.MyAnswerTest"
```

---

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
