# Transcript — q077_market_settlement

Appended automatically on every `review`. Each entry: what changed in `answer/` since the last
entry (diff summary), the test run result, and the in-character exchange.

---

## Review 1 — 2026-09-29

**Tests:** 2/2 ✅

**Diff summary (answer/ vs original seeded code):**
- `BettingSide.java` — new enum: `HOME, AWAY, DRAW`
- `MarketStatus.java` — new enum: `OPEN, SUSPENDED, SETTLED`
- `Market.java` — `String status` → `MarketStatus status`; `double totalPayout` → `BigDecimal totalPayout`
- `Bet.java` — `String side` → `BettingSide side`; `double odds` → `BigDecimal odds`
- `Stake.java` — `double amount` → `BigDecimal amount`; `String currency` → `java.util.Currency currency`
- `MarketSettlementService.java` — param `String winningSide` → `BettingSide winningSide`; payout accumulation uses `BigDecimal`; status guards use `MarketStatus` enum; `@Transactional` added
- `MyAnswerTest.java` — updated to use enums and `BigDecimal`; added guard test for already-settled market

**In-character exchange:**

*Junior A:* "Oh that's clean — the `"home"` lowercase incident literally cannot happen now. The compiler won't let you pass a raw string to `settleMarket`. I like it."

*Junior B:* "Yeah, and `BigDecimal` for odds and stake — good catch. `double` would've given us payout rounding errors on the payout audit. We got flagged on that in the banking team last year."

*Junior A pauses on Stake:* "One thing I'm curious about — you used `java.util.Currency` for the currency field. That's interesting. It validates the ISO 4217 code at construction time — `Currency.getInstance("FAKE")` throws. Is that intentional or did it just feel like the right type?"

*Junior B:* "Also — the `@Transactional` on `settleMarket`: if `betRepository.save(bet)` throws halfway through the loop, the transaction rolls back. But the bets that were already saved before the failure — are they actually safe? Or do they commit individually because each `save` is its own flush?"

---

**Two things worth thinking about before `done`:**

1. **Payout assertion missing.** Both tests verify `isWon()`/`isSettled()` and market status, but nothing asserts the `totalPayout` value. That's the number that drives actual money movement — a test that lets a wrong payout sail through is a gap a code reviewer would flag.

2. **`@Transactional` without Spring context.** It's on the service method, which is correct production code — but your test constructs `MarketSettlementService` directly (no Spring). The annotation does nothing in the test. That's fine for a unit test, but worth knowing: if you want to verify transactional rollback behaviour, you'd need a `@SpringBootTest` or Testcontainers setup. Not a bug — just something to be able to explain if asked.

---
