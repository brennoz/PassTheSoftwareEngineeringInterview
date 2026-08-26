package com.interview.ood.q059_game_provider_integration.solution;

import java.math.BigDecimal;

// Pattern: Adapter (Concrete Adapter) — wraps SlotForgeClient, the simpler of the two
// providers: one blocking call, no handshake. The real adapting work here is UNIT
// translation (pounds <-> pence), not shape translation.
//
// Why `longValueExact()` on the way out, not `longValue()`: if a caller ever passes a stake
// with more than 2 decimal places (a bad upstream calc producing e.g. £1.005), longValue()
// would silently truncate to the wrong pence amount — a real-money bug that would only show
// up as a discrepancy in reconciliation days later. longValueExact() throws immediately
// instead, at the exact point the bad value entered this adapter.
public class SlotForgeAdapter implements GameProviderAdapter {

    private final SlotForgeClient client;

    public SlotForgeAdapter(SlotForgeClient client) {
        this.client = client;
    }

    @Override
    public SpinResult spin(GameSession session, Money stake) {
        long stakeInPence = stake.amount().movePointRight(2).longValueExact();
        SlotForgeSpinResponse response = client.spin(session.sessionId(), stakeInPence);
        Money winAmount = new Money(
                BigDecimal.valueOf(response.winAmountInPence()).movePointLeft(2),
                stake.currency());
        return new SpinResult(winAmount, response.symbols(), response.win());
    }
}
