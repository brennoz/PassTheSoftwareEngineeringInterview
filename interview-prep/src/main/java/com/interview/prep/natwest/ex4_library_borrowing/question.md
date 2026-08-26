# Ex4 — Library Borrowing System

**Round:** NatWest — 2nd interview (live technical coding exercise)
**Format note:** the real exercise hands you *existing* partial code to extend, not a blank file — this practice version is still blank-slate (consistent with Ex1–3) so you rehearse writing the whole flow yourself; the muscle that transfers is TDD discipline and modelling, not memorising this exact skeleton.
**Topics:** TDD ordering/discipline, OOD (information-expert, thin orchestrator service), Mockito `@Spy` vs `@Mock`, `ReentrantLock` as a stretch goal
**Time budget:** Part A ~20–25 min, Part B (stretch) ~10–15 min if time allows

---

## Scenario

A small library system. Members borrow and return physical copies of books. Multiple physical copies of the same title can exist; each copy is independently available or on loan.

## Part A — core (do this first, get it fully green before touching Part B)

Design and implement, from a blank file, whatever classes you think this needs (this is deliberately open — model it how you'd model it live):

1. Register a book with N physical copies, and register a member.
2. `borrowBook(memberId, isbn)` — lends out any one available copy, marks it unavailable, and returns *which* copy was lent.
3. Borrowing a book with zero available copies fails clearly (an exception, not a silent no-op or `null`).
4. `returnBook(memberId, copyId)` — makes the copy available again.
5. Returning a copy the member never borrowed fails clearly — don't let it silently free up someone else's book.
6. Record an audit event ("member X borrowed/returned copy Y") through a collaborator interface (not inline `System.out.println` or a hardcoded implementation) — and write a test that verifies this using a **Mockito `@Spy`**, not a `@Mock`. Be ready to explain, out loud, why spy is the right tool here and not mock.

Write each test before its implementation, in an order that reflects real dependencies between the test cases — the order itself is a signal graders look for, not just whether tests exist.

## Part B — stretch goal (only after Part A is fully green)

Real NatWest sessions are usually simple/no-real-world-complexity by design — but graders reward candidates who show forethought about production behaviour once the base case works. Extend your solution:

1. Identify the actual race condition: what happens if two threads call `borrowBook` for the same book at the same moment, and only one copy is left?
2. Fix it with the smallest correct synchronization — think about exactly which two operations need to become one atomic step, and which object should own that lock.
3. Write a concurrency test with several threads racing for the last copy — assert **exactly one** succeeds, not "probably one."

## Constraints

- No framework — plain Java 21, JUnit 5 + Mockito only (this repo's convention — no AssertJ).
- No `System.out.println` inside the domain/service classes — a `Main` class calling them and printing results is fine, mixing print statements into business logic is not.

## What a strong answer demonstrates

- A coherent object model, not everything crammed into one class — the NatWest feedback specifically flagged candidates with "no coherent approach to the model."
- Test order that reflects real dependency between cases (happy path → the failure that needs the happy path's model → the round trip → the harder edge case → the collaborator interaction → concurrency last).
- Correctly explains `@Spy` vs `@Mock`: spy wraps a REAL object (real methods run unless stubbed) — used here so the test verifies both the interaction (`verify(...)`) and the real resulting state (`allEvents()` actually contains the event), which a full mock couldn't give you without manually faking storage.
- If you get to Part B: correctly names the race (check-then-act on `available`), and puts the lock on the right object — the individual copy, not a library-wide lock (which would serialize borrowing of unrelated books for no reason).

## If you get stuck

Full reference in `solution/` — `Book`, `BookCopy`, `Member`, `Library`, two exceptions, `AuditTrail` + `InMemoryAuditTrail`, `LibraryMain` runnable demo, and `SolutionTest` (6 tests, comments explain the order they were written in and why). Write your own attempt in `answer/`.
