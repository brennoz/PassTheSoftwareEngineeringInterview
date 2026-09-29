# Ex1 — ThreadSafeLedger

**Round:** BankCo — 2nd interview (live technical coding exercise)
**Topics:** `ReentrantLock`, `tryLock` with timeout, deadlock avoidance
**Time budget:** ~15–20 minutes

---

## Scenario

You're building a small in-memory ledger for a bank. Multiple threads (representing concurrent API requests) will call withdraw/deposit/transfer on the same accounts at the same time. The naive version — a plain `HashMap<String, BigDecimal>` with no synchronization — loses updates under concurrency and must not ship.

## Requirements

Design and implement, from a blank file, a `Ledger` (plus whatever supporting classes you think are appropriate — this is deliberately open-ended, structure it how you'd structure it live):

1. `open(Account account)` — register a new account.
2. `withdraw(String accountId, BigDecimal amount)` — thread-safe; throws if funds are insufficient.
3. `deposit(String accountId, BigDecimal amount)` — thread-safe.
4. `transfer(String fromId, String toId, BigDecimal amount)` — thread-safe; moves money between two accounts **without ever deadlocking**, even when two threads transfer in opposite directions between the same pair of accounts simultaneously.
5. Locking must not hang forever — use `tryLock` with a timeout, not a plain `lock()` that can block indefinitely.

## Constraints

- No framework, no Spring — plain Java 21 + `java.util.concurrent`.
- `BigDecimal` for money (this course's standing rule — never `double`/`float`).
- Write the failing test **first** — this is the exercise, not an afterthought. At minimum you need one test that proves thread-safety under real concurrency (not just a single-threaded happy path) and one that proves the transfer method doesn't deadlock.

## What a strong answer demonstrates

- Correct choice of `ReentrantLock` over `synchronized` (and can explain why — `tryLock(timeout)` is the reason).
- Recognizes the deadlock risk in `transfer` (two locks, two possible acquisition orders) and fixes it with consistent lock ordering — not by accident, and can explain the dining-philosophers shape of the bug if asked.
- A concurrency test that would actually fail against a naive/unsynchronized implementation — not just a test that happens to pass regardless.
- Locks released in `finally`, always, even on exception.

## If you get stuck

A full reference implementation with detailed inline explanations is in `solution/` (`Account.java`, `Ledger.java`, exceptions, `LedgerMain.java` runnable demo, and `SolutionTest.java`) — read it before or after attempting, whichever helps you more. Write your own attempt in `answer/` regardless; the point is building the muscle memory of writing this from nothing under time pressure, not copying a working reference.

Run `LedgerMain.main()` to see the concurrency behavior printed, not just asserted in a test.
