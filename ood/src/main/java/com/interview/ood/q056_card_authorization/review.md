# q056 Review — Card Authorization: Template Method

**Result: FAIL — 2/2 of your own tests pass, but they don't exercise the actual requirements.
Verified against the spec directly: 4 of 6 scenarios checked produce the wrong result.**

---

## What You Got Right

- Template Method structure itself is correct: `authorize()` stays `final`, step order
  (validate → hook → network → log) matches the spec, and `validateCard()`/`logResult()` are
  correctly inherited unchanged by both subclasses.
- The hook-vs-abstract MECHANICS are right: `VisaAuthorizationProcessor` correctly doesn't
  override `requiresManualReview()`, `MastercardAuthorizationProcessor` correctly does. You
  understood which method needed which treatment.
- `AuthorizationResult` factory methods are well-formed and immutable.
- Expired-card validation (`validateCard`) is correct.

The problem is entirely in the concrete threshold logic inside `requiresManualReview()` and
`contactIssuerNetwork()`, not the pattern's shape.

---

## Bugs Found (verified by running actual scenarios against your code, not just reading it)

### Bug 1 — [Requirement violation] Hook default is not `false`

**File:** `answer/CardAuthorizationProcessor.java:33-35`
```java
protected boolean requiresManualReview(BigDecimal amount) {
    return amount.compareTo(BigDecimal.valueOf(5000)) > 0;
}
```

The spec is explicit: *"`protected boolean requiresManualReview(BigDecimal amount)` — hook,
default `false`"*. Your base class default has its own review threshold instead of `false`.

**Consequence:** Since `VisaAuthorizationProcessor` correctly does NOT override this hook, it
inherits this threshold — which directly violates requirement #5: *"Visa does not override
`requiresManualReview` — uses the default — never triggers manual review."*

**Verified:**
```
Visa, amount=6000  -> PENDING_REVIEW   (spec says: DECLINED, network reached)
Visa, amount=50000 -> PENDING_REVIEW   (spec says: DECLINED, Visa NEVER reviews)
```

**Fix:**
```java
protected boolean requiresManualReview(BigDecimal amount) {
    return false;
}
```

---

### Bug 2 — [Dead code / logic bug] Visa can never return DECLINED

**File:** `answer/VisaAuthorizationProcessor.java:9-13`
```java
if (!requiresManualReview(amount)) {
    String authorizationCode = "V-" + card.cardNumber().substring(card.cardNumber().length() - 4);
    return AuthorizationResult.approved(authorizationCode);
}
return AuthorizationResult.declined("Card " + card.cardNumber() + " has been declined");
```

`contactIssuerNetwork()` is only ever called by `authorize()` AFTER `requiresManualReview()`
has already returned `false` (that's the whole point of the hook short-circuiting). So inside
this method, `!requiresManualReview(amount)` is **always true** — the `declined(...)` branch
on the last line is unreachable dead code. Combined with Bug 1, Visa currently can only ever
return `APPROVED` or `PENDING_REVIEW` — never `DECLINED` — which contradicts requirement #5's
"approve if ≤5000, otherwise decline."

**Fix:** compare `amount` directly against the approval limit — don't re-check the hook inside
the network step, the template method already guarantees it passed:
```java
protected AuthorizationResult contactIssuerNetwork(Card card, BigDecimal amount) {
    String lastFour = card.cardNumber().substring(card.cardNumber().length() - 4);
    if (amount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
        return AuthorizationResult.approved("V-" + lastFour);
    }
    return AuthorizationResult.declined("Card " + card.cardNumber() + " has been declined");
}
```

---

### Bug 3 — [Inverted logic] Mastercard approves and declines backwards

**File:** `answer/MasterCardAuhtorizationProcessor.java:9-17`
```java
if (amount.compareTo(BigDecimal.valueOf(2000)) > 0) {
    // approves when amount > 2000
    return AuthorizationResult.approved(authorizationCode);
}
return AuthorizationResult.declined(...);  // declines when amount <= 2000
```

Spec requirement #6: approve if `amount <= 2000`, decline otherwise. Your comparison is
backwards.

**Verified:**
```
Mastercard, amount=2000 -> DECLINED   (spec says: APPROVED)
Mastercard, amount=2500 -> APPROVED   (spec says: DECLINED, network reached)
```

**Fix:** flip the comparison direction (same shape as the Bug 2 fix above, with Mastercard's
own 2000 limit and `MC-` prefix — see Bug 4).

---

### Bug 4 — [Spec deviation] Wrong authorization code prefix

**File:** `answer/MasterCardAuhtorizationProcessor.java:13`

Uses `"C-" + lastFour`. Spec requirement #6 asks for `"MC-" + lastFour`.

---

## Minor Notes

- Both `contactIssuerNetwork()` implementations re-check `requiresManualReview()` even though
  the template method already guarantees it's `false` by the time this method runs — this is
  the direct cause of Bug 2 for Visa, and redundant/misleading even where it happens not to
  break anything (Mastercard).
- Class name typo: `MasterCardAuhtorizationProcessor` → `MastercardAuthorizationProcessor`
  (also note the spec/lesson use `Mastercard`, one word, not `MasterCard`).
- `AuthorizationResult.declined()`/`.pendingReview()` set `authorizationCode` to the literal
  string `"not authorized"` rather than `null`. Not wrong per the stated requirements (they
  don't pin this down), just a different valid design choice from the reference solution.

## Test Coverage Gaps

Your 2 tests both pass because both amounts (4999 for Visa, 3000 for Mastercard) happen to
land below every buggy threshold above — neither test would have caught any of Bugs 1–4.
Missing from the suite, all of which are named directly in the question's constraints:

1. **The `final` check** — the question explicitly says *"this is checked by a test via
   reflection (`Modifier.isFinal(...)`), not just by convention"* — not present at all.
2. **An expired-card test** — `validateCard()` throwing is untested.
3. **A decline-path test** for either processor — you only ever tested the approve path.
4. **A manual-review/hook-short-circuit test** — this is the actual point of the whole
   exercise (the question's constraints section says explicitly: *"`contactIssuerNetwork`
   must never be called when `requiresManualReview` returned `true`"*), and it's never
   exercised. This is exactly the test that would have caught Bug 1 immediately.

---

## What to Fix

1. Change `CardAuthorizationProcessor.requiresManualReview()`'s default to `return false;`
2. Rewrite `VisaAuthorizationProcessor.contactIssuerNetwork()` to compare `amount` against the
   limit directly, not re-check the hook
3. Flip the comparison direction in
   `MasterCardAuhtorizationProcessor.contactIssuerNetwork()`
4. Fix the `"C-"` → `"MC-"` prefix
5. Add the 4 missing test cases above — write the manual-review short-circuit test FIRST,
   since it's the one that would have caught the root-cause bug (Bug 1) directly

The Template Method scaffolding (which method is final, which is abstract, which is a hook,
inheritance structure) is genuinely correct — this is a logic-bug fix, not a redesign.
