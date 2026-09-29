package com.interview.pairingdrill.q077_market_settlement;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MarketSettlementService {

    private final MarketRepository marketRepository;
    private final BetRepository betRepository;

    public MarketSettlementService(MarketRepository marketRepository, BetRepository betRepository) {
        this.marketRepository = marketRepository;
        this.betRepository = betRepository;
    }

    public void settleMarket(String marketId, String winningSide) {
        Market market = marketRepository.findById(marketId);

        if (!market.getStatus().equals("OPEN") && !market.getStatus().equals("SUSPENDED")) {
            throw new IllegalStateException(
                "Market " + marketId + " cannot be settled — status: " + market.getStatus());
        }

        List<Bet> bets = betRepository.findByMarketId(marketId);

        double totalPayout = 0;
        for (Bet bet : bets) {
            if (bet.getSide().equals(winningSide)) {
                double payout = bet.getOdds() * bet.getStake().getAmount();
                totalPayout += payout;
                bet.setSettled(true);
                bet.setWon(true);
            } else {
                bet.setSettled(true);
                bet.setWon(false);
            }
            betRepository.save(bet);
        }

        market.setStatus("SETTLED");
        market.setTotalPayout(totalPayout);
        marketRepository.save(market);
    }
}
