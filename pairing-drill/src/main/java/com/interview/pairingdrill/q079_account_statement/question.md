# q079 — Account Statement Service

**Category:** pairing-drill  
**Flavor:** Code-review hybrid (find the bugs + refactor the structure)  
**Domain:** Banking  
**Difficulty:** Medium (4–6 planted issues)  
**Seniority:** Junior  
**Ideal time:** 45–60 min

---

## Scenario

Emma (junior dev, 6 months on the team) wrote `AccountStatementService` to generate monthly
statements for the bank's account holders. It passed her local tests.

The PM has flagged three reports:

1. **Batch statement jobs silently return empty results** — no error, no log, just nothing.
2. **Totals on some statements don't match the ledger** — noticed on accounts with many
   small pence-value transactions.
3. **Performance complaint from the overnight settlement job** — it takes 4× longer than expected
   when processing large account batches.

Emma and Liam are already at the keyboard when you join the pairing session.

---

## Files

| File | Purpose |
|---|---|
| `AccountStatementService.java` | Main service — this is where the issues live |
| `Transaction.java` | Domain entity |
| `Account.java` | Domain entity |
| `Statement.java` | Return type from the service |
| `AccountRepository.java` | Repository interface |
| `TransactionRepository.java` | Repository interface |

---

## Running the tests

```bash
# your answer tests only (run this after every change)
mvn test -pl pairing-drill -Dtest="**/q079**/answer/*Test"

# solution tests (reference — all should stay green)
mvn test -pl pairing-drill -Dtest="**/q079**/solution/*Test"

# all q079 tests at once
mvn test -pl pairing-drill -Dtest="**/q079**/*Test"

# full pairing-drill module
mvn test -pl pairing-drill
```

---

## Your task

Work in `answer/AccountStatementService.java` (and supporting files as needed).

1. Find what's wrong — name each issue, the production consequence, and the fix.
2. Fix each issue in code.
3. Write tests in `answer/MyAnswerTest.java` to cover the fixed behaviour.
4. Refactor the structural smell — the fix should eliminate it, not just patch it.

Use `review` for a non-terminal check-in. Use `done` when finished.

---

## AWS / Distributed Environment

| Issue | AWS / distributed impact |
|---|---|
| Swallowed exception | On ECS/Lambda: silent null propagates up the call chain, CloudWatch shows no error, on-call never pages — invisible outage. Wrap with an error handler that publishes to a dead-letter queue or CloudWatch metric. |
| `double` totals | On any multi-region deployment, floating-point errors are deterministic per CPU architecture — reports generated in us-east-1 vs eu-west-1 may differ by a fraction of a cent for the same account. Use `BigDecimal` everywhere and store amounts as `NUMERIC(19,4)` in RDS. |
| N+1 repo calls | On RDS: N accounts = N separate DB connections. Under RDS Proxy connection pooling, each connection is a pool slot. 10,000 accounts in a batch = 10,000 slots exhausted before the first statement is returned. One `findByAccountIds` bulk query uses one slot. |
| Sensitive data in logs | On CloudWatch Logs / Datadog: log lines are searchable and retained. Logging full account numbers violates GDPR Art. 5(1)(c) and UK FCA data minimisation rules. CloudWatch doesn't know which fields are PII — masking must happen before the log line is written. |
