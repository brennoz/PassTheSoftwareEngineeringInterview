package com.interview.pairingdrill.q080_live_odds_service;

public class OddsSnapshot {

    private final String marketId;
    private final double odds;
    private final long version;

    public OddsSnapshot(String marketId, double odds, long version) {
        this.marketId = marketId;
        this.odds = odds;
        this.version = version;
    }

    public String getMarketId() { return marketId; }
    public double getOdds()     { return odds; }
    public long getVersion()    { return version; }
}
