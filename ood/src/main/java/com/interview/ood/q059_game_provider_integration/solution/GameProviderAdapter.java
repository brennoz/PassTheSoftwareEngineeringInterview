package com.interview.ood.q059_game_provider_integration.solution;

// Pattern: Adapter (Target interface) — this is the ONLY contract the rest of the system
// (GameProviderRegistry, callers) ever depends on. Neither provider's actual SDK shape
// (sync vs async, pence-as-long vs pounds-as-double, handshake-required vs not) leaks past
// this boundary. Adding a third provider later means writing one new adapter class — nothing
// else in the system changes. What NOT to do: expose provider-specific methods (e.g.
// `pollReelMasterX(ticketId)`) on this interface "just in case a caller needs them" — that
// re-leaks the exact vendor detail the pattern exists to hide, and forces every OTHER
// adapter to either implement a meaningless no-op or the interface stops being a true
// abstraction.
public interface GameProviderAdapter {
    SpinResult spin(GameSession session, Money stake);
}
