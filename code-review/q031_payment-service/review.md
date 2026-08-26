# q031 — Code Review: Payment Service · Review

**Result: PASS — 5/5 planted issues found + valid extension on #1**

---

## Scoring

| # | Issue | Found? | Category |
|---|-------|--------|----------|
| 1 | `double` for `amount` | ✅ | Java / Money |
| 2 | PAN (card number) in log | ✅ | Security / PCI-DSS |
| 3 | No `@Transactional` | ✅ | Spring |
| 4 | Idempotency key received but never checked | ✅ | Distributed systems |
| 5 | `HashMap` not thread-safe in singleton `@Service` | ✅ | Concurrency |
| +1 | `calculateFee` returns `double` | ✅ valid extension of #1 |

---

## Per-issue feedback

### 1 + 6 — `double` for monetary values

**What you said:** amount and calculateFee should use BigDecimal. Correct.

`double` uses IEEE 754 floating point. `0.1 + 0.2 == 0.30000000000000004`.
For a £99.99 payment with a 1.5% fee: `99.99 * 1.5 / 100 = 1.4998500000000001`.
At scale (millions of transactions) this accumulates into real money discrepancies —
and regulatory audits will catch it.

Fix:
```java
public BigDecimal calculateFee(BigDecimal amount, BigDecimal feePercent) {
    return amount.multiply(feePercent)
                 .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
}
```

**Note the `RoundingMode`** — `BigDecimal.divide()` throws `ArithmeticException` on non-terminating decimals unless you specify scale + rounding.

---

### 2 — PAN in log (PCI-DSS violation)

**What you said:** card info in log is unsecure. Correct.

```java
log.info("Processing payment: card={}, amount={}", cardNumber, amount);
```

PAN (Primary Account Number) in application logs = immediate PCI-DSS scope expansion.
Every system that ingests those logs (Splunk, CloudWatch, ELK) now stores cardholder data —
must be encrypted, audited, and access-controlled. One log line fails a QSA audit.

Fix: never log PAN. Log the last 4 digits max:
```java
String masked = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
log.info("Processing payment: card={}, amount={}", masked, amount);
```

In AWS: CloudWatch log groups containing PAN must be encrypted with KMS — automatic fail if not.

---

### 3 — No `@Transactional`

**What you said:** processPayment should be transactional. Correct.

```java
account.setBalance(account.getBalance() - amount);
accountRepository.save(account);          // ← succeeds

Payment payment = new Payment(...);
paymentRepository.save(payment);          // ← fails (DB timeout, constraint, etc.)
```

Balance deducted. Payment record never created. Money gone, no record.
No compensating transaction. No audit trail. Impossible to reconcile.

Fix:
```java
@Transactional
public PaymentResult processPayment(...) { ... }
```

Both saves now participate in one ACID transaction. If `paymentRepository.save()` throws,
the `accountRepository.save()` rolls back automatically.

---

### 4 — Idempotency key not checked before processing

**What you said:** key should be checked first to avoid duplication. Correct.

The method signature accepts `idempotencyKey` but only **writes** it after processing.
It never reads it to detect duplicates. Client retries (network timeout, 500 error) hit
the endpoint again → payment processes twice → double charge.

Fix — check DB (not the in-memory map) before processing:
```java
@Transactional
public PaymentResult processPayment(..., String idempotencyKey) {
    paymentRepository.findByIdempotencyKey(idempotencyKey)
        .ifPresent(existing -> { return PaymentResult.success(existing.getId()); });
    // ... rest of logic
}
```

The idempotency record must persist in the **database**, not an in-memory map —
which brings us to issue 5.

---

### 5 — `HashMap` in singleton `@Service` — two distinct problems

**What you said:** ConcurrentHashMap to avoid race conditions. Correct — but only half the fix.

**Problem A — thread safety (single pod):**
Spring `@Service` beans are singletons. Multiple request threads share the same `recentPayments` instance.
Concurrent `put()` on `HashMap` can corrupt the internal structure, cause infinite loops, or silently drop entries.
`ConcurrentHashMap` fixes this.

**Problem B — in-memory state on multi-pod deployment (still broken after ConcurrentHashMap):**
On ECS/Kubernetes with 4 pods:
- Request 1 hits Pod 1 → payment processed → stored in Pod 1's map
- Retry hits Pod 2 → Pod 2's map is empty → duplicate payment processed

`ConcurrentHashMap` is safe per-pod, but idempotency state must be **shared across pods**.

Real fix: store idempotency key in the **database** (a column on the `payments` table with a unique constraint),
or in **Redis** (with TTL). The in-memory map should be removed entirely.

```sql
ALTER TABLE payments ADD COLUMN idempotency_key VARCHAR(64) UNIQUE;
```

In AWS: ElastiCache Redis is the standard idempotency store for payment services —
atomic `SET NX EX` (set-if-not-exists + expiry) is a single round trip with no race condition.

---

## What you missed (not planted — bonus awareness)

### Race condition on account balance

```java
// Thread A and Thread B both execute concurrently:
if (account.getBalance() < amount) { ... }        // both pass: balance = 100, amount = 80
account.setBalance(account.getBalance() - amount); // both deduct 80
accountRepository.save(account);                   // last write wins: balance = 20, not -60
```

Even with `@Transactional`, at default isolation (`READ_COMMITTED`) two threads can both read
`balance = 100`, both pass the insufficient-funds check, and both deduct — resulting in overdraft.

Fix: `@Version` on `Account` (JPA optimistic lock):
```java
@Entity
public class Account {
    @Version
    private Long version;
    // ...
}
```

One thread wins, the other gets `OptimisticLockException` → retry or return `CONCURRENT_MODIFICATION` error.

In payment fintech this is a **top-5 interview topic**. Interviewers will always ask:
"What happens if two payments hit the same account at the same millisecond?"

---

## Summary

5/5 planted issues found. Strong awareness of PCI-DSS and idempotency.
The key refinement: ConcurrentHashMap solves thread safety within one pod,
but idempotency at scale requires DB or Redis — in-memory state is wrong in any distributed deployment.

Add `@Version` to your mental checklist whenever you see a balance mutation without a lock.
