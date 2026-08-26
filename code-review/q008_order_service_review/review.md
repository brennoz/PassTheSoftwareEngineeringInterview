# Review — Q008 Order Service

**Result: ❌ Fail**  
**Score: 2 / 6 planted issues found**

---

## Found ✅

1. **Field injection** — correct. Constructor injection preferred: explicit, final, testable.
2. **`totalOrdersProcessed` race condition** — correct. Missed distributed aspect: 3 pods = 3 independent counters = useless. Fix: `AtomicInteger` for thread safety; Redis `INCR` for distributed count.

## Missed ❌

3. **`SimpleDateFormat` not thread-safe** — static shared instance. Concurrent `format()` calls corrupt results silently. Fix: `DateTimeFormatter` (Java 8, immutable) or new instance per call.

4. **`summaryCache = new HashMap<>()`** — two issues:
   - Not thread-safe: concurrent `put`/`get` corrupts map → `ConcurrentHashMap`
   - Distributed pitfall: each of 3 pods has own stale cache, no invalidation, no TTL, unbounded memory → Redis with TTL

5. **N+1 query in loop** — `order.getItems()` (1 SELECT) + `item.getProduct().getName()` per item (N SELECTs) = N+1. Fix: `JOIN FETCH` in JPQL or `@EntityGraph`.

6. **Swallowed exception** — `catch (Exception e) { // ignore }` hides failures silently. Fix: log at minimum, or let propagate.

## Bonus (sharp candidates catch this)

7. **Missing `@Transactional` on `placeOrder`** — `save()` succeeds, `sendConfirmation()` throws → order saved, no rollback. Fix: `@Transactional` (or Outbox pattern if notification goes via Kafka).

---

## Study Priority
- SimpleDateFormat thread-safety → Java concurrency basics
- In-memory cache in distributed env → distributed pitfalls
- N+1 query → JPA/Hibernate fundamentals (most common JPA interview topic)
