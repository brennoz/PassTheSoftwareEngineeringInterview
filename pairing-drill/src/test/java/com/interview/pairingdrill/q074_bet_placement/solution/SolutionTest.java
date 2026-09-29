package com.interview.pairingdrill.q074_bet_placement.solution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SolutionTest {

    private final BetRepository betRepository = Mockito.mock(BetRepository.class);
    private final BetPlacementService service = new BetPlacementService(betRepository);

    @Test
    void rejectsSuspendedMarket() {
        BetResult result = service.placeBet(
                new Bet("b1", new BigDecimal("10"), new BigDecimal("2")), new Market("m1", "SUSPENDED"));
        assertFalse(result.isAccepted());
    }

    @Test
    void rejectsHighStakeInPlayBet() {
        BetResult result = service.placeBet(
                new Bet("b1", new BigDecimal("600"), new BigDecimal("2")), new Market("m1", "IN_PLAY"));
        assertFalse(result.isAccepted());
    }

    @Test
    void acceptsOpenMarketBetUnderLiabilityLimit() {
        BetResult result = service.placeBet(
                new Bet("b1", new BigDecimal("100"), new BigDecimal("2")), new Market("m1", "OPEN"));
        assertTrue(result.isAccepted());
    }

    // This is the regression test for the actual reported issue: 50 concurrent bets, each with
    // payout 300, on the same market with a 10000 limit. At most 33 can be accepted
    // (33 * 300 = 9900 <= 10000, 34 * 300 = 10200 > 10000) — the check-then-act race in the
    // seeded code would let more than that through under concurrent load, silently exceeding
    // the limit exactly like the reported Saturday incident.
    @Test
    void neverExceedsLiabilityLimitUnderConcurrentLoad() throws Exception {
        Market market = new Market("m1", "OPEN");
        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Callable<BetResult>> tasks = new java.util.ArrayList<>();
        for (int i = 0; i < 50; i++) {
            int idx = i;
            tasks.add(() -> service.placeBet(
                    new Bet("b" + idx, new BigDecimal("100"), new BigDecimal("3")), market));
        }

        List<BetResult> results = Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            List<java.util.concurrent.Future<BetResult>> futures = pool.invokeAll(tasks);
            List<BetResult> collected = new java.util.ArrayList<>();
            for (java.util.concurrent.Future<BetResult> f : futures) {
                collected.add(f.get());
            }
            return collected;
        });
        pool.shutdown();

        AtomicInteger accepted = new AtomicInteger(0);
        results.forEach(r -> { if (r.isAccepted()) accepted.incrementAndGet(); });

        // 300 payout per accepted bet (stake 100 * odds 3); must never total past 10000.
        assertTrue(accepted.get() * 300 <= 10000,
                "accepted " + accepted.get() + " bets, liability " + (accepted.get() * 300) + " exceeds 10000");
    }
}
