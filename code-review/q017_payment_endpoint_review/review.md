# Review — Q017 Payment Endpoint Review

**Result: ❌ Fail**  
**Score: 1.5 / 3 planted issues**

---

## Found ✅

1. **`Optional.get()` without check — lines 20, 23, 32** ✅
   Three `.get()` calls with no guard. Account deleted/archived → `NoSuchElementException` → unhandled 500.
   Fix: `.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"))`.

2. **Idempotency — ½ credit**
   Named it but didn't articulate the consequence: network timeout → client retries POST `/transfer` → two transfers processed → **double debit**.
   Fix: `idempotencyKey` in `TransferRequest`, check `existsByIdempotencyKey()` before processing.
   **AWS:** DynamoDB conditional write (`attribute_not_exists(idempotencyKey)`) is standard idempotency store.

**Bonus — constructor injection + @Transactional ✅**
   Valid catches. Field injection not testable. Missing `@Transactional` on transfer = partial failure leaves data inconsistent.

---

## Missed ❌

3. **Returning `Account` entity directly — lines 26, 32**
   JPA entity exposes: full balance, account number, `@Version` field, all internal fields.
   - ALB access logs → balance in CloudWatch → PCI-DSS violation
   - `@Version` leaked → client could forge optimistic lock bypass
   Fix: map to `AccountResponseDto` with only safe fields (masked account number, transactionId).

---

## Progress

| Pattern | q012 | q014 | q015 | q016 | q017 |
|---|---|---|---|---|---|
| == on String | ❌ | ❌ | ✅ | ✅ | — |
| @Transactional | ✅ | ❌ | ✅ | ✅ | ✅ bonus |
| Idempotency | — | — | ❌ | ❌ | ½ |
| Optional.get() | ✅ | ✅ | — | ❌ | ✅ |
| Returning entity | ❌ | — | — | ❌ | ❌ |

**Rule to memorise:** controller method returns `@Entity` type → immediate flag. Always DTO.
