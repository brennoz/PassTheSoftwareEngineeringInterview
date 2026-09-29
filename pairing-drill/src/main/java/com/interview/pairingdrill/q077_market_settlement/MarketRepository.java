package com.interview.pairingdrill.q077_market_settlement;

public interface MarketRepository {
    Market findById(String marketId);
    void save(Market market);
}
