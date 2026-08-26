package com.interview.ood.q059_game_provider_integration.solution;

import java.util.List;

// Fake third-party SDK #1 — given, synchronous, single call. Fixed-response constructor is
// a deliberate test-double simplification (a real SDK would hit HTTP); what we're modeling
// is the SHAPE (pence, long, one blocking call) that SlotForgeAdapter has to normalize away,
// not real network behaviour.
public class SlotForgeClient {

    private final long winAmountInPence;
    private final List<String> symbols;
    private final boolean win;

    public SlotForgeClient(long winAmountInPence, List<String> symbols, boolean win) {
        this.winAmountInPence = winAmountInPence;
        this.symbols = symbols;
        this.win = win;
    }

    public SlotForgeSpinResponse spin(String sessionId, long stakeInPence) {
        return new SlotForgeSpinResponse(winAmountInPence, symbols, win);
    }
}
