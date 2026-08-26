# Review — Q015 Payment Processor

**Result: ❌ Fail**  
**Score: 4 / 6 planted issues**

---

## Found ✅

1. **PAN in logs — Security / PCI-DSS** ✅
   `log.info("... card={}", request.getCardNumber())` → card number in Splunk/CloudWatch logs = PCI-DSS Level 1 violation. At Visa this would trigger a compliance incident. Fix: log only last 4 digits: `request.getCardNumber().substring(12)` or use a masked token.

2. **Missing `@Transactional`** ✅
   `accountRepository.save()` + `paymentRepository.save()` + `snsClient.publish()` are three separate operations with no transaction boundary. If SNS publish throws, balance is deducted and payment is saved but no event published → ghost payment. Fix: `@Transactional` on the method — or better, use the **Outbox pattern** (write event to DB table in same transaction, publish to SNS asynchronously).
   
   **AWS context:** on RDS Multi-AZ with 4 ECS tasks, an ECS task crash between save() and publish() leaves the DB in an inconsistent state — no retry mechanism. Outbox + SQS/SNS ensures exactly-once delivery.

3. **`payment.getStatus() == "PENDING"` — Java bug** ✅ 
   First time you caught this — good. JPA-loaded strings are heap objects; `==` compares references → always false → status never set to COMPLETED. Fix: `"PENDING".equals(payment.getStatus())`.

4. **Constructor injection missing** ✅ — bonus, correct.

5. **Hardcoded ARN with `us-east-1`** ✅
   `arn:aws:sns:us-east-1:...` — hardcoded region. When eu-west-1 becomes active during failover, this service still publishes to us-east-1 SNS → cross-region latency spike + single point of failure. Fix: inject region via env var `${AWS_REGION}` and build ARN dynamically, or use regional SNS endpoints per deployment.

6. **SNS in separate thread** — partially valid but not the core issue. Async publish doesn't solve atomicity — if the thread fails silently you still lose the event. The right answer is Outbox pattern (covered in #2).

---

## Missed ❌

**Race condition on balance — Data integrity (most critical miss)**

```java
if (account.getBalance() < request.getAmount()) { return INSUFFICIENT_FUNDS; }
account.setBalance(account.getBalance() - request.getAmount());
accountRepository.save(account);
```

At 5,000 tx/sec with 4 ECS tasks: two requests for the same card arrive simultaneously on different nodes. Both read balance = £100. Both see £100 ≥ £60. Both deduct £60. Account ends at £40 — customer is charged £120 on a £100 account. **This is an overdraft / double-spend bug.**

Fix options (in order of preference):
- `@Version` on `Account` entity → JPA optimistic locking → second write throws `OptimisticLockException`, retry
- `SELECT ... FOR UPDATE` (pessimistic lock) → serialises access but hurts throughput at 5k tx/sec
- DynamoDB conditional write: `UpdateItem` with `ConditionExpression: balance >= :amount` → atomic check-and-deduct

**AWS context:** RDS Aurora with Multi-AZ does NOT prevent this — the race happens at the application layer, not DB layer. `@Version` / optimistic locking is the standard fix for payment processors.

---

**No idempotency key — Data integrity (Visa-critical)**

Client retries on timeout → `processPayment()` called twice with same payload → two `Payment` records created → **customer charged twice**.

Fix: accept an `idempotencyKey` in `PaymentRequest`. Before processing, check: `paymentRepository.findByIdempotencyKey(key)`. If exists, return cached result. Store key with TTL (24h). 

**AWS context:** DynamoDB conditional writes (`PutItem` with `attribute_not_exists(idempotencyKey)`) are the standard AWS pattern for idempotent payment APIs.

---

## Summary

| Issue | Found? |
|---|---|
| PAN in logs (PCI-DSS) | ✅ |
| Missing @Transactional / Outbox | ✅ |
| == on String | ✅ **first time caught** |
| Hardcoded AWS region | ✅ |
| Race condition / no optimistic lock | ❌ |
| No idempotency key | ❌ |

**Biggest gaps:** race condition (overdraft) and idempotency (double charge) — the two most Visa-critical issues in the whole snippet. Both relate to concurrent distributed access patterns.
