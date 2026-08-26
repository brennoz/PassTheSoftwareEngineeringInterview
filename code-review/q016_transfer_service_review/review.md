# Review — Q016 Fund Transfer Service

**Result: ❌ Fail (but best code-review attempt yet)**  
**Score: 3 / 6 planted issues + 3 strong bonus observations**

---

## Found ✅

1. **Constructor injection** ✅ — bonus, not planted but correct.

2. **Missing `@Transactional` on `transfer()`** ✅
   Three `save()` calls + external `auditService.log()` with no transaction boundary. If `accountRepository.save(to)` succeeds but `transactionRepository.save(tx)` fails → `from` debited, `to` credited, no transaction record. Money moved with no audit trail.
   **AWS:** Aurora Multi-AZ doesn't protect against application-level partial writes. `@Transactional` + Spring's `PlatformTransactionManager` wraps all three saves in one ACID unit. If any step throws → full rollback on Aurora.

3. **`@Version` on Account for optimistic locking** ✅ — excellent, shows you learned from q015.
   Two ECS instances process transfers from the same account simultaneously → both read balance = £500 → both deduct £300 → overdraft. `@Version` on `Account` entity causes the second write to throw `OptimisticLockException` → client retries with fresh data.
   **AWS:** Aurora Multi-AZ with multiple ECS tasks: this race is guaranteed at 5k tx/sec without optimistic locking.

4. **Outbox pattern instead of `auditService.log(tx)`** ✅ — excellent bonus.
   Synchronous external call inside a transaction holds the DB connection for the duration of the HTTP call. If audit API is slow (200ms+), you're holding a connection pool slot. If audit API is down, the whole transfer fails.
   Correct fix: write audit event to outbox table in same transaction, async Kafka consumer delivers to audit service. This is exactly the **Outbox pattern** and is the standard answer in payment network interviews.
   **AWS:** SQS or MSK (Kafka) as the async transport; audit consumer on a separate ECS service. Decoupled, fault-tolerant.

5. **`tx.getStatus() == "COMPLETED"` — Java bug** ✅ — caught consistently now. Good.

---

## Missed ❌

6. **N+1 query in `getHistory()`**
   ```java
   for (Transaction tx : transactions) {
       Account account = accountRepository.findById(tx.getFromAccountId()).get(); // 1 SELECT per tx
   ```
   100 transactions = 101 SELECTs (1 + 100). At scale on Aurora this hammers the DB.
   You said "this block is unnecessary — remove it." That avoids the N+1 but misses the diagnosis. If the data IS needed, fix with a JOIN FETCH or `@EntityGraph`. If not needed, remove the field population.
   **AWS:** on Aurora read replicas, N+1 = 100 read replica round-trips per request. Use `JOIN FETCH` to collapse to 1 query.

7. **`Optional.get()` without check**
   ```java
   accountRepository.findById(tx.getFromAccountId()).get()
   ```
   Throws `NoSuchElementException` if account was deleted/archived → unhandled 500 to caller.
   Fix: `.orElseThrow(() -> new ResponseStatusException(NOT_FOUND))`.

8. **`return from` — returning entity directly (security)**
   `Account` entity returned from controller exposes: full balance, account number, internal JPA fields, version number, potentially other sensitive fields.
   Fix: map to a `TransferResponseDto` with only fields the client needs (transactionId, maskedAccountNumber, new balance if appropriate).
   **AWS + PCI:** if this response is logged by ALB access logs or API Gateway, the full account balance and number are in CloudWatch Logs → compliance violation.

9. **No idempotency key (missed again)**
   Network timeout on client → client retries → two identical transfers processed → double debit.
   Fix: `idempotencyKey` field in `TransferRequest`, check `existsByIdempotencyKey()` before processing, store result with TTL.
   **AWS:** DynamoDB conditional write (`attribute_not_exists`) is the standard idempotency store for payment APIs.

---

## Progress Summary

| Pattern | q008 | q010 | q012 | q014 | q015 | q016 |
|---|---|---|---|---|---|---|
| == on String | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ |
| @Transactional | ❌ | ❌ | ✅ | ❌ | ✅ | ✅ |
| Idempotency | — | — | — | — | ❌ | ❌ |
| Optional.get() | ❌ | — | ✅ | ✅ | — | ❌ |
| Returning entity | — | — | ❌ | — | — | ❌ |
| N+1 query | ❌ | — | — | — | — | ½ |
| Optimistic lock | — | — | — | — | ❌ | ✅ |
| Outbox pattern | — | — | — | — | — | ✅ |

Strong improvement. Idempotency and entity-as-DTO are the remaining blind spots.
