package com.interview.pairingdrill.q080_live_odds_service.solution;

import java.math.BigDecimal;

// Redeclared in solution package because odds is now BigDecimal (fix 5), not double.
// Why BigDecimal: double 2.50 is exact in IEEE 754, but 1.65 is not (1.6499999...998).
// Payout = stake * odds — a rounding error on every bet times millions of bets = real money lost.
public class OddsSnapshot {

    private final String marketId;
    private final BigDecimal odds;
    private final long version;

    public OddsSnapshot(String marketId, BigDecimal odds, long version) {
        this.marketId = marketId;
        this.odds = odds;
        this.version = version;
    }

    public String getMarketId()  { return marketId; }
    public BigDecimal getOdds()  { return odds; }
    public long getVersion()     { return version; }
}
