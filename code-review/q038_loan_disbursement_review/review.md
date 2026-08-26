# q038 Review — Loan Disbursement Service

**Result: ✅ PASS — 5/5 planted issues found**

---

## Planted Issues vs Your Findings

### Issue 1 — Java Style: `==` on String ✅ FOUND
**Line ~38, ~51:** `loan.getStatus() == "PENDING"` and `loan.getStatus() == "APPROVED"`

String literals in source are interned. `getStatus()` returns a heap String from the DB.
`==` compares references — this will be `false` in prod even when the value matches.
Both conditions silently never execute → `approve()` never sets APPROVED; `disburse()` never moves money.

**Fix:** `.equals("PENDING")`, or better: introduce a `LoanStatus` enum to eliminate string comparison entirely.

**AWS/distributed:** Same bug on every pod — consistent wrongness. But if you ever compare strings from two different classloaders (OSGi, WAR deployments), interning breaks anyway. Enum is the fix.

---

### Issue 2 — Banking trigger: `double` for monetary amount ✅ FOUND
**Line ~10, ~34:** `@RequestParam double amount` parameter; `double approvedAmount`

Floating-point precision loss on monetary values — `0.1 + 0.2 == 0.30000000000000004`.
At loan scale (£50,000.00), double rounding can silently misrepresent the approved amount.
FCA audit: loan records must contain exact approved amounts. Regulatory non-compliance.

**Fix:** `BigDecimal approvedAmount` everywhere. In the controller use `String` as `@RequestParam` and parse to `BigDecimal`.

---

### Issue 3 — JPA/Hibernate: Entity as DTO (approve) ✅ FOUND
**Line ~12:** `public LoanApplication approveLoan(...)` returns `LoanApplication` JPA entity directly.

Leaks DB schema to API consumers. Hibernate proxies with lazy collections serialised by Jackson → `LazyInitializationException` if transaction is closed before response serialisation. Also couples API contract to DB schema.

**Fix:** Map to `LoanApplicationDto` before returning from controller. Use a mapper (MapStruct or manual).

**AWS/distributed:** On multi-pod ECS, session closes before Jackson serialises → `LazyInitializationException` is consistent and reproducible at load.

---

### Issue 4 — Spring: Missing `@Transactional` on `disburse()` ✅ FOUND
**Line ~43:** `disburse()` has no `@Transactional`.

Two writes: `accountRepo.save(account)` → then `loanRepo.save(loan)`.
If JVM crashes or DB times out between the two saves:
- Account is credited (money moved)
- Loan status stays `"APPROVED"` → next call to disburse() re-executes → **double disbursement**

**Fix:** `@Transactional` on `disburse()`. Both saves commit or both roll back.

**AWS/distributed:** On RDS Multi-AZ failover between the two saves, the standby replica promotes and the second save lands on a new connection with no transaction context. `@Transactional` + connection pool (HikariCP) + RDS Proxy mitigates this.

Also noted: `approve()` should be `@Transactional` too (though single save, guard is still correct practice for consistency).

---

### Issue 5 — Banking trigger: Missing audit log ✅ FOUND
**`auditLogger`** is `@Autowired` but never called in `approve()` or `disburse()`.

**Consequence:** No immutable record of who approved what amount or when money was disbursed.
SOX compliance requires audit trail on every balance-changing operation.
In a regulatory audit, missing disbursement logs = serious compliance failure.

**Fix:** Call `auditLogger.log(...)` in both `approve()` and `disburse()` after successful state changes.

**On the CompletableFuture suggestion:** Async audit log is dangerous — if the async task fails after the DB commit, you have a disbursement with no audit record. Use synchronous logging in the same `@Transactional` boundary. If audit must be decoupled, use the **Outbox pattern** (write audit event to same DB table, deliver via change-data-capture).

**AWS:** Outbox + DynamoDB Streams → Lambda → S3 with Object Lock (WORM) = compliant, durable audit trail.

---

## Bonus Findings (valid, not planted)

### Bonus A — Entity list from `getPendingLoans()` ✅ FOUND
`loanRepo.findAll()` returns ALL loans (not just PENDING) — method name is a lie.
Also returns entities — same DTO leakage issue as Issue 3.
**Fix:** `loanRepo.findByStatus(LoanStatus.PENDING)` and map to DTOs.

### Bonus B — Idempotency check missing ✅ FOUND
Valid for `disburse()` — concurrent or retried calls can double-disburse even with `@Transactional` (depends on isolation level). A `@Version` optimistic lock or a unique constraint on `disbursementId` prevents it.

### No credit
- **Caching suggestion** — not a bug; premature optimisation without a stated bottleneck.

---

## What you missed

### REST Design — `@GetMapping` on state-changing endpoint
`@GetMapping("/{id}/approve")` — GET must be safe and idempotent (RFC 7231).
A GET that changes loan status violates HTTP semantics.
Browsers, CDNs, and load balancers freely cache or prefetch GETs.
**Fix:** `@PatchMapping` or `@PostMapping`.

---

## Score

| Issue | Category | Found |
|---|---|---|
| `== String` comparison | Java Style | ✅ |
| `double` for monetary amount | Banking trigger | ✅ |
| Entity as DTO (approve) | JPA/Hibernate | ✅ |
| Missing `@Transactional` on disburse | Spring | ✅ |
| Missing audit log (SOX) | Banking trigger | ✅ |
| `@GetMapping` on state-change | REST Design | ❌ missed |

**5/5 planted. Strong pass.** Bonus: entity list, idempotency — both sharp catches.

---

## Pattern to carry forward

`@GetMapping` for state mutation is easy to miss because it compiles and works in dev.
In prod: CDN edge caches the GET → approval fires once but browser loads cached 200 response from edge, not the server → user sees "approved" but second attempt re-triggers the approve flow. Always pair mutation verbs (POST/PATCH/PUT/DELETE) with state-changing operations.
