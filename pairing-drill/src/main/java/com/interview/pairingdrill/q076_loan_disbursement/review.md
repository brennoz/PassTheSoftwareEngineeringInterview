# Review — q076 Loan Disbursement

**Result: PASS**
**Date:** 2026-09-25
**Flavor:** Spring-specific · Banking domain · Junior pairing partners
**Tests:** 4/4 green

---

## Planted issues vs found

| # | Issue | Found | Fixed |
|---|---|---|---|
| 1 | `@Autowired` field injection — hides dependencies, requires reflection or Spring context to unit-test | ✅ | ✅ constructor injection |
| 2 | No `@Transactional` — 3 separate saves with no rollback boundary; crash between any two → APPROVED status with no ledger entry | ✅ | ✅ `@Transactional` on `disburseLoan` |
| 3 | No idempotency guard — no version check, no duplicate detection → double disbursement on client retry | ✅ | ✅ `checkBeforeProcessing` + `DuplicateDisbursementException` + `version` field on `LoanAccount` |
| 4 | No timeout on `creditBureauClient.checkCreditScore()` — blocking call with no limit → thread hangs, connection pool exhaustion under load | ✅ | ✅ `checkCreditScoreWithTimeout` via `CompletableFuture` |

All 4 planted issues identified and fixed. 4/4.

---

## Pairing rubric

### 1. Technical communication
**Strong.** Oriented correctly before touching code — stated what the seeded service did step by step, identified the two problems in the incident description (duplicate disbursement and testability complaint) as distinct concerns before writing a line. Didn't conflate `@Transactional` with race protection — correctly identified that both are needed for different failure modes.

### 2. Balanced engineering
**Strong.** Used `CompletableFuture.supplyAsync().get(timeout)` rather than pulling in resilience4j for a single call site. Correct call — the module doesn't need the full dependency for one timeout. The solution reference did the same thing and says as much in its comment. Didn't over-engineer the version check either — `checkBeforeProcessing` already existed in the repository interface, used it directly.

### 3. Prioritization
**Good.** Tackled the incident-causing bug (duplicate disbursement) first, then `@Transactional`, then the timeout. Right order — the incident was the stated trigger, cascading failure risk was secondary.

### 4. Reading unfamiliar code
**Strong.** Spotted the `checkBeforeProcessing(Long id, long expectedVersion)` signature and asked the right question: "where does `expectedVersion` come from if `LoanAccount` has no version field?" — demonstrated reading the interface carefully, not just the service.

### 5. Leading safely
**Good.** Incremental — added the guard, then the transaction annotation, then the timeout wrapper. Ran tests at each meaningful checkpoint. Asked about the race condition vs status-check distinction before coding.

---

## Minor notes (not rubric deductions)

- **`InterruptedException | ExecutionException` multi-catch:** `Thread.currentThread().interrupt()` is semantically correct only for `InterruptedException`. For `ExecutionException` it's harmless but wrong — that exception doesn't set the interrupted flag. Worth splitting into two separate catch blocks.
- **Slow timeout test:** `wrapsASlowCreditBureauInATimeout` sleeps for 4 seconds (longer than the 3s timeout). Correct and necessary for the assertion, but adds ~4s to every suite run. Acceptable trade-off at this scale; in a large suite you'd tag it as `@Tag("slow")` and exclude from the fast CI gate.

---

## AWS / Distributed impact

- **`@Transactional` alone is not enough in a multi-instance deployment.** On a 4-pod ECS cluster, each pod's `@Transactional` only scopes its own JDBC connection. Two pods can both read `PENDING`, both pass a status-only check, both disburse. The version-based `checkBeforeProcessing` is what actually prevents this — it's a `WHERE version = ?` conditional update at the DB level, and the DB serialises concurrent writers. This is the correct fix for a distributed environment, not just a local guard.
- **`creditBureauClient` timeout:** In AWS, this would typically be a `resilience4j` `@TimeLimiter` + `@CircuitBreaker` backed by an ECS/Lambda external call. The circuit breaker is what the plain `CompletableFuture` doesn't give you — if the bureau is consistently slow, you want to fast-fail rather than spawning new threads for every request.
- **`LoanAccount.version`** maps directly to `@Version Long version` in JPA with Hibernate — Hibernate generates the conditional `UPDATE ... WHERE version = ?` automatically when you call `save()`.
