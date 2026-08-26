# Review — Q024 Bet Placement Service Review

**Result: ❌ Fail**  
**Score: 3 / 5 planted issues**

---

## Found ✅

1. **`Optional.get()` without check — line 22** ✅
2. **Missing `@Transactional` — lines 32–42** ✅ — wallet debited, bet save fails → money gone
3. **`static liabilityCount` — lines 17, 44** ✅ — not thread-safe + per-JVM → use Redis INCR

**Bonus:** `@Version` on Wallet ✅, constructor injection ✅

---

## Missed ❌

4. **`double` for stake/odds/payout — lines 24, 29, 30, 32**
   Floating-point arithmetic → wrong payouts (`1.10 * 50.0 = 55.00000000000001`).
   Regulatory liability. Fix: `BigDecimal` for all monetary values and odds.

5. **No idempotency key** — timeout → retry → duplicate bet → double deduction.
   Fix: `idempotencyKey` in `PlaceBetRequest`, check before processing.
   AWS: DynamoDB conditional write (`attribute_not_exists`).

---

## Rules
- `double`/`float` touching money, odds, payout → `BigDecimal`. Always.
- POST/PUT mutation with no idempotency check → always flag.
