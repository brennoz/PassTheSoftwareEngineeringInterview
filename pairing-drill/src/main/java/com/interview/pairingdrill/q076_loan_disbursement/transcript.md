# Transcript — q076_loan_disbursement

Appended automatically on every `review`. Each entry: what changed in `answer/` since the last
entry (diff summary), the test run result, and the in-character exchange.

---

## Session — 2026-09-25

### Diff summary (answer/ vs seeded)
- `LoanAccount`: added `version: long` field, 4-arg constructor, `getVersion()` getter
- `LoanDisbursementService`: added `@Transactional`, idempotency guard via `checkBeforeProcessing`, extracted `checkCreditScoreWithTimeout` using `CompletableFuture.supplyAsync().get(3s, SECONDS)`, `CreditCheckTimeoutException` on timeout
- `MyAnswerTest`: 4 tests — happy path, credit rejection, duplicate guard, slow credit bureau timeout

### Test run
```
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 4.645s — BUILD SUCCESS
```

### In-character exchange

**Junior A:** The duplicate guard looks exactly right — throws before touching anything, credit bureau never called. Exactly what we needed from the incident.

**Junior B:** `@Transactional` is there too. The 3-save chain without it was the partial-failure trap — status says APPROVED but no ledger entry.

**Junior A:** The timeout wrapper is clean. One nit: the `InterruptedException | ExecutionException` multi-catch calls `Thread.currentThread().interrupt()` for both branches — that's only semantically correct for `InterruptedException`. For `ExecutionException` it's harmless but wrong. Worth splitting those two catches.

**Junior B:** And that timeout test takes 3 seconds every run — it's correct but slow. Not a blocker, just worth knowing.

---
