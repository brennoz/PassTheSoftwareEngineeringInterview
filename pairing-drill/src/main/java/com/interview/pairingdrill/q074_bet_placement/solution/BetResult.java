package com.interview.pairingdrill.q074_bet_placement.solution;

public class BetResult {
    private final boolean accepted;
    private final String reason;

    private BetResult(boolean accepted, String reason) { this.accepted = accepted; this.reason = reason; }
    public static BetResult accepted() { return new BetResult(true, null); }
    public static BetResult rejected(String reason) { return new BetResult(false, reason); }
    public boolean isAccepted() { return accepted; }
    public String getReason() { return reason; }
}
