package com.interview.ood.q059_game_provider_integration.solution;

import java.util.List;

// Third-party response shape — given, not ours to change. Pence as `long`, not BigDecimal:
// this vendor's own convention, absorbed at the adapter boundary.
public record SlotForgeSpinResponse(long winAmountInPence, List<String> symbols, boolean win) {
}
