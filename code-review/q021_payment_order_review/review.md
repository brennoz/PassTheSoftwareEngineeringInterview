# Review — Q021 Payment Order Controller Review

**Result: ✅ Pass — first clean code-review pass**  
**Score: 3 / 3 planted issues**

---

## Found ✅

1. **Returning `PaymentOrder` entity directly — lines 26, 31** ✅
   Both endpoints return `@Entity` directly. Exposes: card token, internal JPA fields, `@Version`, all DB columns.
   - `cardToken` in response → PCI-DSS violation
   - ALB / API Gateway logs full order → CloudWatch → compliance fail
   Fix: `PaymentOrderResponseDto` with only: `orderId`, `status`, maskedCardLast4, `amount`.

2. **No idempotency key** ✅
   POST `/orders` has no idempotency check. Timeout → client retry → two charges on same card.
   Fix: `Idempotency-Key` header. Check `existsByIdempotencyKey()` before processing. Store result with 24h TTL.
   **AWS:** DynamoDB conditional write (`attribute_not_exists`) — atomic across multiple ECS pods.

3. **`Optional.get()` without check — line 32** ✅
   Order not found → `NoSuchElementException` → unhandled 500.
   Fix: `.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"))`.

**Bonus:**
- Constructor injection instead of field `@Autowired` ✅
- `@Transactional` on `createOrder()` ✅ — `save()` + `charge()` must be atomic; partial failure = ghost order.

---

## Progress

| Pattern | q015 | q016 | q017 | q021 |
|---|---|---|---|---|
| Idempotency | ❌ | ❌ | ½ | ✅ |
| Returning entity | — | ❌ | ❌ | ✅ |
| Optional.get() | — | ❌ | ✅ | ✅ |
| @Transactional | ✅ | ✅ | ✅ bonus | ✅ bonus |

Level up to medium difficulty for next code-review.
