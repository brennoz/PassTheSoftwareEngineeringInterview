# Review — Q014 Report Service

**Result: ❌ Fail**  
**Score: 3 / 6 planted issues**

---

## Found ✅

1. **Constructor injection missing** ✅ — bonus, not planted but correct.

2. **`Optional.get()` without check** ✅ — correct. `report.get()` throws `NoSuchElementException` if ID not found → unhandled 500. Fix: `.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))`.

3. **SQL injection** ✅ — correct. String concat with user input = OWASP #1. Fix: `jdbcTemplate.query("SELECT * FROM reports WHERE status = ? AND customer_id = ?", mapper, status, customerId)`.

4. **`HashMap` not thread-safe** ✅ partial — correct that it needs `ConcurrentHashMap`. But missed the **bigger distributed pitfall**: 4 nodes = 4 separate in-memory caches with stale/inconsistent data. A report updated on node 1 stays cached as old data on nodes 2-4. Fix: Redis distributed cache with TTL.

---

## Missed ❌

5. **`report.getStatus() == "PENDING"` — Java bug (3rd time missed)**
```java
if (report.getStatus() == "PENDING")  // always false for DB-loaded strings
```
Strings from JPA/JDBC are new String objects — `==` compares references, not values. This `if` block **never executes** — no reports ever get processed. Fix: `"PENDING".equals(report.getStatus())`.
This has appeared in q010 and q014. Must become a reflex — any `== "string_literal"` is a bug.

6. **Swallowed exception loses stack trace**
```java
log.error("Notification failed: " + e.getMessage());
```
Two problems:
- `e.getMessage()` drops the stack trace → impossible to debug in prod
- String concat in log → use placeholders: `log.error("Notification failed", e)` — logs full stack trace automatically.

7. **Missing `@Transactional` on `processAll()`**
Loop does N `save()` calls. If `notificationClient.notify()` throws on record 5, records 1-4 are saved as PROCESSING but no notification sent → inconsistent state. Fix: `@Transactional` on `processAll()`, and move notification outside the transaction (or use Outbox pattern).

---

## Bonus (good catch)

8. **Dead code: `sendNotification()` never called** ✅ — `processAll()` calls `notificationClient.notify()` directly, bypassing `sendNotification()`. Either use the helper or delete it.

---

## Study Priority
- **`==` on Strings** — this is the 3rd question where you missed it. Make it a reflex: any `== "literal"` in Java = instant flag.
- **Log with exception object**, not `e.getMessage()`: `log.error("msg", e)` not `log.error("msg: " + e.getMessage())`
