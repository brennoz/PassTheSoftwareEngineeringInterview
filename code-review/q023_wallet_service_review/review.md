# Review — Q023 Wallet Service Review

**Result: ❌ Fail**  
**Score: 3 / 6 planted issues**

---

## Found ✅

1. **`Optional.get()` without check — line 20** ✅
   `NoSuchElementException` → 500 if wallet not found.
   Fix: `.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Wallet not found"))`.

2. **Missing `@Transactional` on `deposit()` — lines 27–33** ✅
   `walletRepository.save()` succeeds → `transactionRepository.save()` throws → balance updated, no record.
   AWS: Aurora doesn't protect against app-level partial writes.

3. **Missing `@Version` — race condition on balance — line 27** ✅
   Two ECS pods read same balance simultaneously → lost update → money disappears.
   Fix: `@Version` on `Wallet` → second write throws `OptimisticLockException`.

**Bonus — constructor injection** ✅

---

## Missed ❌

4. **`SimpleDateFormat` instance field — lines 16, 31**
   Not thread-safe. Spring singleton = shared across all threads. Concurrent calls → garbled timestamps or NPE.
   Fix: `DateTimeFormatter` (Java 8, immutable, thread-safe).

5. **Returning `Wallet` entity — line 19** ❌ (q012, q016, q023 — 3rd miss)
   Exposes internal JPA fields, `@Version` counter, sensitive balance data.
   Fix: return `WalletDto` with only fields caller needs.

6. **N+1 query — line 41**
   1 SELECT per wallet in loop. 100 wallets = 101 SELECTs on Aurora.
   Fix: `countByWalletIdIn(ids)` — one grouped query.

---

## Progress

| Pattern | q021 | q023 |
|---|---|---|
| Returning entity | ✅ | ❌ regressed |
| @Transactional | ✅ bonus | ✅ |
| Optional.get() | ✅ | ✅ |
| @Version | — | ✅ |
| N+1 | — | ❌ |
| SimpleDateFormat | — | ❌ |

Rule: every method return type in service/controller — if it's `@Entity`, flag immediately.
SimpleDateFormat as instance field in Spring bean = always a bug.
