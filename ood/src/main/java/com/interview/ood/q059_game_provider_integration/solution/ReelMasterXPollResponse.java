package com.interview.ood.q059_game_provider_integration.solution;

import java.util.List;

// Third-party response shape — given. Pounds as `double`, not BigDecimal: this vendor's own
// (poor) convention. The adapter converts to BigDecimal at the boundary via
// BigDecimal.valueOf(double), never `new BigDecimal(double)` — see ReelMasterXAdapter.
public record ReelMasterXPollResponse(String status, double winAmountInPounds, List<String> symbols) {
}
