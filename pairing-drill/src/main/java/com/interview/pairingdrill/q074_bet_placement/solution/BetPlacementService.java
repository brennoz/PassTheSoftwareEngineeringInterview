package com.interview.pairingdrill.q074_bet_placement.solution;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class BetPlacementService {

    // Why ConcurrentHashMap, not HashMap: this map is shared across every concurrent request
    // hitting a singleton service. HashMap corrupts under concurrent writes; ConcurrentHashMap
    // is safe for concurrent get/put, but on its own it does NOT fix the actual bug here — see
    // compute() below.
    private final Map<String, BigDecimal> marketLiability = new ConcurrentHashMap<>();
    private final BigDecimal maxLiabilityPerMarket = new BigDecimal("10000.00");

    private final BetRepository betRepository;

    public BetPlacementService(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    public BetResult placeBet(Bet bet, Market market) {
        BetResult statusRejection = MarketStatus.valueOf(market.getStatus()).checkAcceptance(bet);
        if (statusRejection != null) {
            return statusRejection;
        }

        BigDecimal potentialPayout = bet.getStake().multiply(bet.getOdds());
        AtomicBoolean accepted = new AtomicBoolean(false);

        // Why compute(), not getOrDefault-then-put: the seeded bug was a check-then-act race —
        // read currentLiability, decide, then write, as three separate un-synchronized steps.
        // Two concurrent bets on the same market could both read the same currentLiability
        // before either writes, both pass the threshold check, and the loser's write silently
        // overwrites the winner's contribution — liability limit exceeded with no error
        // anywhere. compute() runs its whole read-modify-write atomically under the map's
        // per-key lock, so the two concurrent calls serialize instead of racing.
        marketLiability.compute(market.getId(), (id, currentLiability) -> {
            BigDecimal current = currentLiability == null ? BigDecimal.ZERO : currentLiability;
            BigDecimal newLiability = current.add(potentialPayout);
            if (newLiability.compareTo(maxLiabilityPerMarket) > 0) {
                return current; // reject: leave liability exactly as it was
            }
            accepted.set(true);
            return newLiability;
        });

        if (!accepted.get()) {
            return BetResult.rejected("Market liability limit exceeded");
        }

        betRepository.save(bet);
        return BetResult.accepted();
    }
}
