package com.interview.pairingdrill.q080_live_odds_service.answer;

import java.math.BigDecimal;

public class OddsSnapshot {

    private final String marketId;
    private final BigDecimal odds;
    private final long version;

    public OddsSnapshot(String marketId, BigDecimal odds, long version) {
        this.marketId = marketId;
        this.odds = odds;
        this.version = version;
    }

    public String getMarketId() { return marketId; }
    public BigDecimal getOdds()     { return odds; }
    public long getVersion()    { return version; }
}
