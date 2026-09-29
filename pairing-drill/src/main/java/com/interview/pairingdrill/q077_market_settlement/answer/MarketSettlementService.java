package com.interview.pairingdrill.q077_market_settlement.answer;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketSettlementService {

    private final MarketRepository marketRepository;
    private final BetRepository betRepository;

    public MarketSettlementService(MarketRepository marketRepository, BetRepository betRepository) {
        this.marketRepository = marketRepository;
        this.betRepository = betRepository;
    }

    @Transactional 
    public void settleMarket(String marketId, BettingSide winningSide) {
        Market market = marketRepository.findById(marketId);

        if (!market.getStatus().equals(MarketStatus.OPEN) && !market.getStatus().equals(MarketStatus.SUSPENDED)) {
            throw new IllegalStateException(
                "Market " + marketId + " cannot be settled — status: " + market.getStatus());
        }

        List<Bet> bets = betRepository.findByMarketId(marketId);

        BigDecimal totalPayout = BigDecimal.ZERO;
        for (Bet bet : bets) {
            if (bet.getSide().equals(winningSide)) {
                BigDecimal payout = bet.getOdds().multiply(bet.getStake().getAmount());
                totalPayout = totalPayout.add(payout);
                bet.setSettled(true);
                bet.setWon(true);
            } else {
                bet.setSettled(true);
                bet.setWon(false);
            }
            betRepository.save(bet);
        }

        market.setStatus(MarketStatus.SETTLED);
        market.setTotalPayout(totalPayout);
        marketRepository.save(market);
    }
}
