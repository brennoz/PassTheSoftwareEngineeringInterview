package com.interview.pairingdrill.q080_live_odds_service.answer;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LiveOddsService {

    private final Map<String, BigDecimal> currentOdds = new ConcurrentHashMap<>();
    private final Set<String> suspendedMarkets = ConcurrentHashMap.newKeySet();
    private final AtomicLong updateCount = new AtomicLong(0);

    private final OddsRepository oddsRepository;
    private final MarketRepository marketRepository;

    public LiveOddsService(OddsRepository oddsRepository, MarketRepository marketRepository) {
        this.oddsRepository = oddsRepository;
        this.marketRepository = marketRepository;
    }

    public synchronized void acceptUpdate(String marketId, BigDecimal newOdds, long version) {
        if (suspendedMarkets.contains(marketId)) {
            System.out.println("Skipping update for suspended market: " + marketId);
            return;
        }
        currentOdds.put(marketId, newOdds);
        updateCount.incrementAndGet();
        oddsRepository.save(new OddsSnapshot(marketId, newOdds, version));
    }

    public synchronized void suspendMarket(String marketId) {
        suspendedMarkets.add(marketId);
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Market not found: " + marketId));
        market.setStatus(MarketStatus.SUSPENDED);
        marketRepository.save(market);
    }

    public synchronized void reopenMarket(String marketId) {
        suspendedMarkets.remove(marketId);
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("Market not found: " + marketId));
        market.setStatus(MarketStatus.ACTIVE);
        marketRepository.save(market);
    }

    public BigDecimal getOdds(String marketId) {
        return currentOdds.getOrDefault(marketId, BigDecimal.ZERO);
    }

    public boolean isSuspended(String marketId) {
        return suspendedMarkets.contains(marketId);
    }

    public AtomicLong getUpdateCount() {
        return updateCount;
    }
}
