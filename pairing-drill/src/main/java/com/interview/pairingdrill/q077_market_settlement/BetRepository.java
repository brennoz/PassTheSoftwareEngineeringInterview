package com.interview.pairingdrill.q077_market_settlement;

import java.util.List;

public interface BetRepository {
    List<Bet> findByMarketId(String marketId);
    void save(Bet bet);
}
