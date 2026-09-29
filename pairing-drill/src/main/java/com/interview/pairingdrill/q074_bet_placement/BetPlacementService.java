package com.interview.pairingdrill.q074_bet_placement;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BetPlacementService {

    private final Map<String, BigDecimal> marketLiability = new HashMap<>();
    private final BigDecimal maxLiabilityPerMarket = new BigDecimal("10000.00");

    private final BetRepository betRepository;

    public BetPlacementService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    public BetResult placeBet(Bet bet, Market market) {
        if (market.getStatus().equals("OPEN")) {
            // normal acceptance path, nothing extra to check here
        } else if (market.getStatus().equals("SUSPENDED")) {
            return BetResult.rejected("Market suspended");
        } else if (market.getStatus().equals("IN_PLAY")) {
            if (bet.getStake().compareTo(new BigDecimal("500.00")) > 0) {
                return BetResult.rejected("Stake too high for in-play market");
            }
        } else if (market.getStatus().equals("CLOSED")) {
            return BetResult.rejected("Market closed");
        }

        BigDecimal currentLiability = marketLiability.getOrDefault(market.getId(), BigDecimal.ZERO);
        BigDecimal potentialPayout = bet.getStake().multiply(bet.getOdds());
        BigDecimal newLiability = currentLiability.add(potentialPayout);

        if (newLiability.compareTo(maxLiabilityPerMarket) > 0) {
            return BetResult.rejected("Market liability limit exceeded");
        }

        marketLiability.put(market.getId(), newLiability);
        betRepository.save(bet);
        return BetResult.accepted();
    }
}
