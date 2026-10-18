package com.interview.pairingdrill.q080_live_odds_service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class LiveOddsService {

    private final Map<String, Double> currentOdds = new HashMap<>();
    private final Set<String> suspendedMarkets = new HashSet<>();
    private long updateCount = 0;

    private final OddsRepository oddsRepository;
    private final MarketRepository marketRepository;

    public LiveOddsService(OddsRepository oddsRepository, MarketRepository marketRepository) {
        this.oddsRepository = oddsRepository;
        this.marketRepository = marketRepository;
    }

    public void acceptUpdate(String marketId, double newOdds, long version) {
        if (suspendedMarkets.contains(marketId)) {
            System.out.println("Skipping update for suspended market: " + marketId);
            return;
        }
        currentOdds.put(marketId, newOdds);
        updateCount++;
        oddsRepository.save(new OddsSnapshot(marketId, newOdds, version));
    }

    public void suspendMarket(String marketId) {
        suspendedMarkets.add(marketId);
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Market not found: " + marketId));
        market.setStatus("SUSPENDED");
        marketRepository.save(market);
    }

    public void reopenMarket(String marketId) {
        suspendedMarkets.remove(marketId);
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Market not found: " + marketId));
        market.setStatus("ACTIVE");
        marketRepository.save(market);
    }

    public double getOdds(String marketId) {
        return currentOdds.getOrDefault(marketId, 0.0);
    }

    public boolean isSuspended(String marketId) {
        return suspendedMarkets.contains(marketId);
    }

    public long getUpdateCount() {
        return updateCount;
    }
}
