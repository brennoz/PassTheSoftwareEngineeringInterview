package com.interview.pairingdrill.q080_live_odds_service.solution;

import java.util.Optional;

// Redeclared in solution package so save() accepts solution.Market (MarketStatus enum).
public interface MarketRepository {
    Optional<Market> findById(String marketId);
    void save(Market market);
}
