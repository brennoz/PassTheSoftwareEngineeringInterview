# Review — Q010 Kafka Payment Consumer

**Result: ❌ Fail**  
**Score: 1.5 / 6 planted issues**

---

## Found ✅

1. **Field injection** — correct. Use constructor injection.
2. **fraudCheckClient blocks** — partially correct. Real issue: `@Transactional` holds DB connection open during external HTTP call. Fix: move fraud check outside transaction boundary.

## Missed ❌

3. **`static int processedCount` — Concurrency + Distributed pitfall**
   Race condition on `processedCount++` (not atomic). Meaningless across pods. Fix: `AtomicInteger`; or Micrometer counter for distributed metrics.

4. **Logging `getCardNumber()` — Security / PCI-DSS**
   Card numbers in logs = PCI-DSS violation. Log aggregators store raw card data. Fix: mask to last 4 digits only.

5. **`event.getStatus() == "PENDING"` — Java bug**
   `==` compares references, not values. Deserialized Kafka strings are new objects → `==` always false → `if` block never runs → status never set. Fix: `"PENDING".equals(event.getStatus())`.

6. **Swallowed exception — Kafka data loss**
   Exception caught → method returns normally → Kafka commits offset → message permanently lost. Fix: rethrow so Kafka retries, or send to dead-letter topic.

## Bonus (sharp candidates)

7. **No idempotency** — Kafka at-least-once: same event arrives twice on rebalance/retry → duplicate Payment records. Fix: check `existsById` before save, or `INSERT ... ON CONFLICT DO NOTHING`.

---

## Study Priority
- `==` vs `.equals()` on Strings — fundamental Java, must know cold
- Swallowed exceptions in Kafka listeners — critical for messaging reliability
- PII/sensitive data in logs — comes up in any payment/fintech interview
- @Transactional + external calls — common Spring anti-pattern
