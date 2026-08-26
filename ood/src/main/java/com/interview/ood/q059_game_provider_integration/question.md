**Lessons:** [0001 Adapter Pattern — Third-Party Game Provider Integration](../../../../../topics/ood/adapter/0001-third-party-game-provider-adapter.html)

# q059 — Third-Party Game Provider Integration

**Category:** OOD · **Difficulty:** Hard · **Ideal time:** 25 min

---

## Context

Your casino platform integrates spins from **two external game providers**, each with a
genuinely different SDK shape (both are given, in `solution/`, and must not be changed):

- **SlotForgeClient** — synchronous, one blocking call. Stakes and win amounts in **pence**,
  as `long`.
- **ReelMasterXClient** — asynchronous. Requires `authenticate(apiKey)` → token, then
  `submitSpin(token, sessionId, stakeInPounds)` → a ticket ID (not a result), then repeated
  `poll(ticketId)` calls until status is `COMPLETE` (or `FAILED`). Stakes and win amounts in
  **pounds**, as `double`.

Your job: design one clean internal interface that hides both shapes from every caller.

---

## Design approach — Adapter pattern

- `GameProviderAdapter` — the **Target** interface: `SpinResult spin(GameSession, Money stake)`
- `SlotForgeAdapter`, `ReelMasterXAdapter` — **Concrete Adapters**, each wraps one given SDK
- `GameProviderRegistry` — thin Facade routing a spin request to the right adapter by
  `session.providerId()`

---

## Requirements

### 1. `Money` (value object)
`BigDecimal amount`, `Currency currency`, non-negative by construction (record + compact
constructor guard).

### 2. `GameSession` (record)
`sessionId`, `playerId`, `providerId`.

### 3. `SpinResult` (record)
`Money winAmount`, `List<String> symbols`, `boolean success`. Static factory
`failed(Currency)` for a failure outcome (zero `Money`, empty symbols, `success=false`).
Instance method `isWin()` — true only if `success` **and** `winAmount > 0`.

### 4. `GameProviderAdapter` (interface)
`SpinResult spin(GameSession session, Money stake)` — the only contract callers depend on.

### 5. `SlotForgeAdapter`
Wraps `SlotForgeClient`. Converts the stake from `Money` (pounds, `BigDecimal`) to pence
(`long`) using `longValueExact()` — must throw, not silently truncate, if the stake has a
sub-pence fraction. Converts the response back from pence to pounds.

### 6. `ReelMasterXAdapter`
Wraps `ReelMasterXClient`. Performs the full handshake (`authenticate` → `submitSpin` →
loop `poll` until `COMPLETE`/`FAILED`), bounded by a max attempt count — must **not** poll
forever if the provider never completes; return `SpinResult.failed(...)` instead. Converts
the double win amount to `BigDecimal` via `BigDecimal.valueOf(double)` — never
`new BigDecimal(double)`.

### 7. `GameProviderRegistry`
Constructor takes `Map<String, GameProviderAdapter>` keyed by provider ID.
`playSpin(GameSession, Money stake)` routes to the matching adapter; throws
`IllegalArgumentException` for an unknown `providerId`.

---

## System design — be ready to discuss, not just code

- Why does the Adapter's Target interface expose **zero** provider-specific detail (no
  `pollReelMasterX(...)` method "just in case")? What breaks if it did, once a third
  provider is added?
- `ReelMasterXAdapter` currently polls with `Thread.sleep` on the caller's thread. What's the
  actual scaling problem with that at high spin volume, and what are the two realistic
  alternatives (push-based webhook/event vs a bounded async wait)?
- If SlotForge starts timing out intermittently, where would you add a Circuit Breaker, and
  why does it belong at the adapter layer rather than in `GameProviderRegistry`?

---

## Constraints

- `BigDecimal` for all money — never `double`/`float` past the adapter boundary
- No `instanceof` checks or raw types
- The two given SDK client classes are fixed — adapt to them, don't modify them

---

## Folder layout

```
ood/src/main/java/.../q059_game_provider_integration/
├── question.md
├── solution/   ← reference implementation, one class per file (open only after attempting)
└── answer/     ← write your implementation here

ood/src/test/java/.../q059_game_provider_integration/
├── solution/SolutionTest.java  ← reference test suite
└── answer/                     ← write your tests here
```

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
