# Review — q079: Account Statement Service

**Flavor:** Code-review hybrid  
**Domain:** Banking  
**Difficulty:** Medium (5 planted + 1 structural)  
**Result:** ✅ Pass  
**Tests:** 4/4 green (answer), 5/5 green (solution)

---

## Issues found and fixed

| # | Issue | Found | Fixed | Notes |
|---|---|---|---|---|
| 1 | `System.out.println` sensitive data logging | ✅ | ✅ | Removed |
| 2 | Swallowed exception → silent null | ✅ | ✅ | `try/catch` removed, `orElseThrow` propagates |
| 3 | `double` for monetary amounts → `BigDecimal` | ✅ | ✅ | All fields: `Transaction.amount`, `Account.balance`, `Statement` totals |
| 4 | `String` type → `TransactionType` enum | ✅ | ✅ | Created `TransactionType.java`, updated `Transaction` |
| 5 | N+1 `findByAccountId` loop in `generateBatch` | ✅ | ✅ | Replaced with `findByAccountIds` bulk fetch |
| 6 | Structural: filter+total logic duplicated | ✅ | ✅ | Extracted to `getStatementByAccount()` |
| Bonus | `Statement` holds raw `Account` entity | — | — | Not addressed — acceptable, bonus-only |

6/6 planted issues found and fixed independently. No hints used.

---

## Test coverage

| Test | What it pins |
|---|---|
| `should_get_bigdecimal_totals` | BigDecimal accumulation via `groupingBy + reducing` |
| `should_exception_propagates_for_unknown_account` | No swallowing — exception reaches caller |
| `should_batch_uses_bulk_fetch` | Totals correct AND `verify(never()).findByAccountId` pins N+1 fix |
| `should_apply_date_range_filtering` | Only in-range transactions included |

Good test structure. `verify(never())` in the batch test is the right instinct — it pins the repo-call behaviour, not just the output. `compareTo` used correctly throughout for BigDecimal equality.

---

## Rubric

### Technical communication
Strong. Named every issue by category (compliance, precision, N+1, structural) without prompting. Explained consequences clearly when asked. No ambiguity about what was being fixed and why.

### Balanced engineering
Good. Chose `Collectors.groupingBy + reducing` for the totals — a real improvement over two separate filter+sum streams. Caught the `getOrDefault` NPE risk independently after it was flagged. Didn't over-engineer: no unnecessary abstractions introduced.

### Prioritization
Good sequence: typed the domain model first (BigDecimal, enum), then fixed the service logic, then improved tests. High-impact issues addressed before lower-priority structural items.

### Reading unfamiliar code
Strong. Identified all six issues from a cold read of the seeded service. Spotted the structural duplication between `generateStatement` and `generateBatch` and extracted the helper without being told which approach to use.

### Leading safely
Good. Fixed issues one at a time with tests running between changes. Did not leave the codebase in a broken state at any intermediate step.

---

## Gaps to carry forward

**1. BigDecimal precision test is weak**  
`should_get_bigdecimal_totals` uses 50.0, 30.0, 100.0, 40.0 — round numbers that are exact in binary floating-point, so the test passes with `double` too. The test that actually distinguishes `BigDecimal` from `double` uses pence values that accumulate floating-point error:
```java
// 3 × £0.10 — double gives 0.30000000000000004, BigDecimal gives 0.30
```
Next time: write the BigDecimal test with values that would fail under `double`.

**2. `equalsIgnoreCase` on accountId (not fixed)**  
Line 46 in `generateBatch`: `.filter(t -> t.getAccountId().equalsIgnoreCase(...))`. Account IDs are identifiers — case-sensitive. `"A1" != "a1"` in any real system. Use `.equals()`. Minor, but systematic: identifiers always use `equals`, human-readable strings can use `equalsIgnoreCase`.

**3. `should_get_bigdecimal_totals` missing zero-of-one-type case**  
All test fixtures include both CREDIT and DEBIT transactions. The `getOrDefault` fix handles an account with only credits (or only debits), but no test covers it. One more fixture with credits-only would close the gap.

---

## AWS / Distributed impact (summary)

| Issue fixed | Distributed consequence avoided |
|---|---|
| Swallowed exception | On ECS/Lambda: silent null propagates, CloudWatch shows no error, on-call never pages |
| `double` totals | Floating-point errors deterministic per CPU arch — statements differ across regions |
| N+1 repo calls | 10k accounts = 10k RDS Proxy connection slots exhausted; one bulk query = one slot |
| `println` logging | CloudWatch Logs retains and indexes stdout — PII exposure, GDPR/FCA violation |
