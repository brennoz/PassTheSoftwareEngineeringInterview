**Lessons:** [0001 Card Authorization: Template Method](../../../../../topics/ood/template-method/0001-card-authorization-template-method.html)

# q056 — Card Authorization: Template Method

**Category:** OOD · **Difficulty:** Medium · **Ideal time:** 25 min

---

## Design approach

A card authorization flow is the same four steps for every card network — validate the card,
check whether the amount needs manual review, contact the issuer network, log the result —
but exactly ONE of those steps (how you actually talk to the network) is genuinely different
per network. That's the shape **Template Method** exists for: a fixed algorithm skeleton in
an abstract base class, with one abstract step every subclass must supply and one optional
"hook" step a subclass may override if it needs stricter behaviour.

Compare this to q043's `SchedulingStrategy` (Strategy pattern) after finishing — same surface
goal (vary behaviour per subtype), very different mechanism and trade-offs. The lesson covers
the distinction in depth.

---

## Requirements

1. `Card(String number, String cardholderName, YearMonth expiry)` — immutable value object
2. `Status` enum: `APPROVED`, `DECLINED`, `PENDING_REVIEW`
3. `AuthorizationResult(Status status, String authorizationCode, String message)` — immutable,
   with factory methods `approved(authorizationCode)`, `declined(message)`,
   `pendingReview()`
4. `CardAuthorizationProcessor` (abstract class):
   - `public final AuthorizationResult authorize(Card card, BigDecimal amount)` — the
     template method. Fixed sequence:
     1. `validateCard(card)` — throws `IllegalArgumentException` if `card.expiry()` is before
        the current month
     2. `requiresManualReview(amount)` — if `true`, return `AuthorizationResult.pendingReview()`
        immediately, **without** calling `contactIssuerNetwork()`
     3. `contactIssuerNetwork(card, amount)` — the result of this call
     4. `logResult(card, result)` — called after step 3, before returning
   - `protected void validateCard(Card card)` — concrete, shared
   - `protected boolean requiresManualReview(BigDecimal amount)` — **hook**, default `false`
   - `protected abstract AuthorizationResult contactIssuerNetwork(Card card, BigDecimal amount)` — **abstract**, every subclass must implement
   - `protected void logResult(Card card, AuthorizationResult result)` — concrete, shared
     (a simple implementation is fine — e.g. append to an in-memory `List<String>` or print;
     the exact form isn't graded, but it must exist and be called)
5. `VisaAuthorizationProcessor extends CardAuthorizationProcessor`:
   - Does **not** override `requiresManualReview` (uses the default — never triggers manual
     review)
   - `contactIssuerNetwork`: approve (with authorization code `"V-" + last 4 digits of card
     number`) if `amount <= 5000`; otherwise decline
6. `MastercardAuthorizationProcessor extends CardAuthorizationProcessor`:
   - Overrides `requiresManualReview`: returns `true` if `amount > 3000`
   - `contactIssuerNetwork` (only ever called for `amount <= 3000`, given the hook above):
     approve (authorization code `"MC-" + last 4 digits`) if `amount <= 2000`; otherwise
     decline

---

## Constraints

- `authorize()` must be `final` — this is checked by a test via reflection
   (`Modifier.isFinal(...)`), not just by convention
- `BigDecimal` for all amounts — never `double`
- No `instanceof` or type-switch anywhere
- `contactIssuerNetwork` must never be called when `requiresManualReview` returned `true` for
  that call — the hook must genuinely short-circuit the template method, not just be checked
  and ignored

---

## Folder layout

(Standard for `next ood` — see CLAUDE.md's "OOD Delivery Convention" section: full
solution-walkthrough lesson + complete `solution/` (including a runnable
`CardAuthorizationMain.java`) + empty `answer/`, mirrored under both `src/main` and `src/test`.)

```
q056_card_authorization/
├── question.md
├── solution/   ← full reference implementation, one class per file, TDD-ordered, includes CardAuthorizationMain.java
└── answer/     ← empty (.gitkeep) — write from scratch, or study the lesson + solution/ first
```

---

## Hints

If stuck → type `hint`
To see full solution → type `solution`
To submit → type `done`
