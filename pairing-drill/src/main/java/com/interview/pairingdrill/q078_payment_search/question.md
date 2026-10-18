# Q078 — Payment Search (Pairing Drill)

**Flavor:** Search / Filtering  
**Domain:** Payment / Fintech  
**Difficulty:** Medium (5–6 planted issues)  
**Category:** pairing-drill

---

## Scene

You've just joined a payments team for a sprint. Two junior engineers — Priya and Leo — are
pair-programming on the payments dashboard backend.

Priya opens Slack: *"The `search` endpoint is slow on large datasets and the product team
keep asking us to add new filters. Every time we add one we have to copy-paste code into
two places. Also Leo found a bug — risk-flagged CARD payments aren't being filtered
correctly when you specify a type. Can you take a look?"*

Leo adds: *"Also, I noticed SearchCriteria uses `double` for amounts. Should that be
BigDecimal? And I feel like the class could be immutable since we never change criteria
after building it?"*

The seeded code is in `PaymentQueryService.java`, `SearchCriteria.java`.

---

## Your Task

Work with Priya and Leo to refactor the code. They'll ask questions — guide them through
the reasoning, not just the fix.

Edit files in `answer/`. Run tests with:

```
mvn test -pl pairing-drill \
  -Dtest="com.interview.pairingdrill.q078_payment_search.answer.MyAnswerTest"
```

---

## Planted Issues (find them yourself — don't read `solution/` yet)

There are **6 planted issues** across two files. The scene gives you enough hints to find
them all.

---

## Files

| File | Role |
|---|---|
| `PaymentQueryService.java` | Seeded: god-method, duplicate logic, missing type filter in findForRisk |
| `SearchCriteria.java` | Seeded: `double` fields, mutable (setters) |
| `answer/` | Your working copy — edit these |
| `solution/` | Full reference with Specification pattern (peek when done) |

---

## Chain Repetition Echo (from last 2 drills)

- **BigDecimal vs double** (q077): watch for `double` in `SearchCriteria` and `.doubleValue()` comparisons
- **Enum == vs .equals()** (q077 review note): String status comparisons here — same category

---

## In AWS / Distributed Environment

| Concern | Impact |
|---|---|
| `repository.findAll()` on every call | At scale (millions of payments) this is a full table scan. In production: push filter criteria to the DB query (JPA Specification or Querydsl); use ElastiCache to cache hot filter combinations. |
| In-memory filter logic | Works on a single node. In a multi-pod ECS/EKS deployment each pod has the same code, so correctness scales — but performance doesn't. The DB is the bottleneck, not the filter. |
| Double precision in compliance context | PAN-adjacent amount fields must be exact. `double` comparisons on payment amounts can misfire at financial precision boundaries — regulatory liability in audited environments. |
| God-method adds new filters | Each new filter requires a deployment. AWS CodePipeline / GitLab CI pipelines must run for every tweak. Specification pattern isolates changes so only the new spec class touches production. |
