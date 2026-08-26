# Review — Q025 Savings Account Service Review

**Result: ✅ Pass**  
**Score: 3 / 3 planted issues**

---

## Found ✅

1. **`Optional.get()` without check — line 13** ✅
2. **Returning `SavingsAccount` entity — lines 12, 28** ✅ caught both endpoints
3. **`double` for interest — lines 21–23, 26** ✅
   Two layers: `.doubleValue()` loses precision, then `new BigDecimal(double)` inherits the error.
   Fix: stay in BigDecimal throughout.
   ```java
   BigDecimal interest = account.getBalance()
       .multiply(account.getAnnualRate())
       .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
   ```

**Bonus:** `@Transactional` ✅, idempotency ✅
