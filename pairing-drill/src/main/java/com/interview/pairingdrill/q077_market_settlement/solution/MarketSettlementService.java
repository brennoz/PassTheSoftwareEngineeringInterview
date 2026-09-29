package com.interview.pairingdrill.q077_market_settlement.solution;

import java.math.BigDecimal;
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

    // Why BettingSide not String: the incident was triggered by a caller passing "home"
    // instead of "HOME". String.equals() is case-sensitive and silent on mismatch — no
    // exception, zero winning bets, zero payout. Changing the param type to BettingSide
    // makes the typo a compile error instead of a runtime financial incident.
    // What NOT to do: keep String and add .toUpperCase() upstream — that just moves the
    // smell one layer up and still lets "HOMM" or "" through silently.
    public void settleMarket(String marketId, BettingSide winningSide) {
        Market market = marketRepository.findById(marketId);

        // Why == not .equals() for enum comparison: enums are singletons — only one instance
        // of MarketStatus.OPEN exists per JVM, so reference equality is semantically correct
        // and avoids the String .equals() pitfall that caused the original incident.
        if (market.getStatus() != MarketStatus.OPEN && market.getStatus() != MarketStatus.SUSPENDED) {
            throw new IllegalStateException(
                "Market " + marketId + " cannot be settled — status: " + market.getStatus());
        }

        List<Bet> bets = betRepository.findByMarketId(marketId);

        // Why forEach + separate stream rather than one loop: separating the side effect
        // (marking won/settled + saving) from the pure reduction (summing payouts) makes
        // each step readable in isolation. A single loop mixing mutation and accumulation
        // is harder to reason about and test independently.
        bets.forEach(bet -> {
            bet.setSettled(true);
            bet.setWon(bet.getSide() == winningSide);
            betRepository.save(bet);
        });

        // Why BigDecimal.add not += on double: floating-point addition on doubles loses
        // precision — 1.75 * 10.0 can produce 17.499999999... rather than 17.50. In a
        // payout sum that lands in a customer's wallet that is a regulatory and financial
        // liability. BigDecimal.multiply + reduce is exact at any scale.
        // Why Bet::isWon not bet -> bet.isWon(): method reference is idiomatic when the
        // lambda body is a single method call with no transformation.
        BigDecimal totalPayout = bets.stream()
                .filter(Bet::isWon)
                .map(bet -> bet.getOdds().multiply(bet.getStake().amount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        market.setStatus(MarketStatus.SETTLED);
        market.setTotalPayout(totalPayout);
        marketRepository.save(market);
    }
}
