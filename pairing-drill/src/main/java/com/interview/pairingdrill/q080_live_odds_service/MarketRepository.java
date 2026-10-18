package com.interview.pairingdrill.q080_live_odds_service;

import java.util.Optional;

public interface MarketRepository {
    Optional<Market> findById(String marketId);
    void save(Market market);
}
