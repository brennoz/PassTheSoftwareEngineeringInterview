# Session State

status: exam
last: q078_payment_search
mode: pairing-drill

## Active Question

**Q078 — payment_search** | pairing-drill | Search/Filtering flavor | Medium (6 issues)

**Status:** In progress — partially done. Resume tomorrow.

**Scene:** Priya + Leo (junior partners).

**6 planted issues — progress:**
1. ✅ `SearchCriteria` `double` → `BigDecimal`
2. ✅ `SearchCriteria` mutable setters → immutable record (with `onlyType()` factory)
3. ✅ `Payment` String status/type → `PaymentStatus`/`PaymentType` enums
4. ⬜ `findForRisk()` missing type filter bug — test written (RED), fix not yet in
5. ⬜ God-method duplication → Specification pattern (`PaymentSpec`, `AmountRangeSpec`, `StatusSpec`, `TypeSpec`, `FlaggedForRiskSpec`, private `buildSpec()`)
6. ⬜ `repository.findAll()` unbounded scan (AWS/distributed note — not a code change)

**Key bug in current `findForRisk` line 37:**
`if (!p.isFlaggedForRisk() || !p.getType().equals(criteria.type())) return false;`
When `criteria.type()` is null → always true → total wipe. Fixed by Specification pattern (null type = TypeSpec not added to chain).

**Test state:**
- `findForRisk_shouldFilterByType_whenTypeSpecified` → RED (expected 1, got 2)
- Uses enums + record already

**Run tests:**
```
mvn test -pl pairing-drill \
  -Dtest="com.interview.pairingdrill.q078_payment_search.answer.MyAnswerTest"
```

**Next step:** implement Specification pattern files in `answer/` (user writes them):
`PaymentSpec.java`, `AmountRangeSpec.java`, `StatusSpec.java`, `TypeSpec.java`, `FlaggedForRiskSpec.java`
Then refactor `PaymentQueryService` with `buildSpec()`.

**Solution:** `solution/` pre-seeded — reference available.
