# Ex3 — ConcurrentTransactionStream (combined)

**Round:** BankCo — 2nd interview (live technical coding exercise)
**Topics:** RxJava `Observable` across threads + `ReentrantLock` + `CompletableFuture` composition — everything from Ex1 and Ex2, together
**Time budget:** ~25–30 minutes

---

## Scenario

Money movements arrive as a live event feed from **several concurrent producers** (imagine several upstream services, or several partitions of a Kafka topic, each posting transactions independently). You need to apply every event to a shared ledger and persist a record of it — without ever losing an update, no matter how the events interleave across threads.

## Requirements

1. A source of transaction events that genuinely emits from **multiple threads** — merge several RxJava `Observable`s, each `subscribeOn(Schedulers.io())`, so events truly race each other rather than arriving from one thread in a nice safe order.
2. A ledger that applies each event to a running balance, protected the same way as Ex1 — `ReentrantLock` per account, `tryLock` with a timeout, never a lost update no matter how many producers fire concurrently.
3. After applying an event, persist a record of the resulting balance via an **async** call (`CompletableFuture`) — this step doesn't block the RxJava consumer thread.
4. A test that proves (3) is real: run with multiple producers × many events each, wait for every persist to complete, and assert the final balance matches an exact expected number, and the persisted-record count equals the total event count.

## Constraints

- Reuse the lock-per-account pattern from Ex1 rather than reinventing it — the point of this exercise is recognizing that RxJava's own scheduler threads are just as real a concurrency source as an `ExecutorService` you spin up yourself, so the same discipline applies.
- Plain Java 21, RxJava 3, `java.util.concurrent`.

## What a strong answer demonstrates

- Correctly identifies that `Observable.merge` + multiple `subscribeOn` schedulers means the subscriber's `onNext` callback can be invoked from different threads — this is the crux of the exercise, and worth saying out loud even if not directly asked.
- Doesn't assume RxJava serializes access for you — locking the shared ledger state is still the caller's job.
- Uses a `CountDownLatch` (or equivalent) to correctly wait for **async persistence**, not just the synchronous ledger updates, before asserting — a common bug is asserting immediately after `subscribe()` returns, before the async persist calls have actually completed.

## If you get stuck

Full reference in `solution/` — `TransactionEvent`, `ConcurrentLedger` (Ex1's lock pattern reused), `TransactionEventSource` (the multi-thread RxJava merge), `PersistenceGateway` (the async persist step), `StreamMain` runnable demo, and `SolutionTest`. Write your own attempt in `answer/`.
