package com.interview.ood.q059_game_provider_integration.solution;

import java.util.List;

// Fake third-party SDK #2 — given, deliberately the AWKWARD shape: requires a handshake
// (authenticate -> token), submitSpin returns a ticket ID immediately (not a result — this
// provider settles spins asynchronously), and the actual outcome only appears once poll()
// reports COMPLETE. `pollsUntilComplete` lets tests control exactly how many polls happen
// before the result is ready (including "never" — see the timeout test) without needing a
// real clock or network.
public class ReelMasterXClient {

    private final int pollsUntilComplete;
    private final double winAmountInPounds;
    private final List<String> symbols;
    private int pollCount = 0;

    public ReelMasterXClient(int pollsUntilComplete, double winAmountInPounds, List<String> symbols) {
        this.pollsUntilComplete = pollsUntilComplete;
        this.winAmountInPounds = winAmountInPounds;
        this.symbols = symbols;
    }

    public String authenticate(String apiKey) {
        return "token-" + apiKey;
    }

    public String submitSpin(String token, String sessionId, double stakeInPounds) {
        return "ticket-" + sessionId;
    }

    public ReelMasterXPollResponse poll(String ticketId) {
        pollCount++;
        if (pollCount < pollsUntilComplete) {
            return new ReelMasterXPollResponse("PENDING", 0.0, List.of());
        }
        return new ReelMasterXPollResponse("COMPLETE", winAmountInPounds, symbols);
    }
}
