package com.interview.pairingdrill.q077_market_settlement.solution;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SolutionTest {

    private final MarketRepository marketRepository = Mockito.mock(MarketRepository.class);
    private final BetRepository betRepository = Mockito.mock(BetRepository.class);

    private final MarketSettlementService service = new MarketSettlementService(marketRepository, betRepository);

    // Why this test is structured this way: it verifies BOTH that the winning bet is marked
    // won AND that the payout is calculated correctly with BigDecimal precision. A version
    // that only asserted isWon() would miss the BigDecimal correctness entirely. A version
    // that only asserted the payout total would miss whether individual bets were marked.
    // Both assertions are load-bearing here.
    @Test
    void settles_winning_bets_and_calculates_total_payout_correctly() {
        // HOME: odds 1.75, stake £10.00 → payout £17.50
        // AWAY: odds 3.00, stake £5.00  → loses, no payout
        Market market = new Market("mkt-1", MarketStatus.OPEN);
        Bet homeBet = new Bet("bet-1", "mkt-1", BettingSide.HOME,
                new BigDecimal("1.75"), new Stake(new BigDecimal("10.00"), "GBP"));
        Bet awayBet = new Bet("bet-2", "mkt-1", BettingSide.AWAY,
                new BigDecimal("3.00"), new Stake(new BigDecimal("5.00"), "GBP"));

        Mockito.when(marketRepository.findById("mkt-1")).thenReturn(market);
        Mockito.when(betRepository.findByMarketId("mkt-1")).thenReturn(List.of(homeBet, awayBet));

        service.settleMarket("mkt-1", BettingSide.HOME);

        assertTrue(homeBet.isWon());
        assertTrue(homeBet.isSettled());
        assertFalse(awayBet.isWon());
        assertTrue(awayBet.isSettled());
        assertEquals(MarketStatus.SETTLED, market.getStatus());
        // compareTo not assertEquals: BigDecimal.equals() is scale-sensitive (17.50 != 17.5)
        assertEquals(0, market.getTotalPayout().compareTo(new BigDecimal("17.50")));
    }

    // Why this test exists separately: it specifically verifies the losing-side path —
    // that totalPayout is ZERO when no bets won. A single happy-path test would not catch
    // a bug where losing bets accidentally contribute to the payout sum.
    @Test
    void total_payout_is_zero_when_no_bets_are_on_winning_side() {
        Market market = new Market("mkt-1", MarketStatus.OPEN);
        Bet awayBet = new Bet("bet-1", "mkt-1", BettingSide.AWAY,
                new BigDecimal("2.50"), new Stake(new BigDecimal("20.00"), "GBP"));

        Mockito.when(marketRepository.findById("mkt-1")).thenReturn(market);
        Mockito.when(betRepository.findByMarketId("mkt-1")).thenReturn(List.of(awayBet));

        service.settleMarket("mkt-1", BettingSide.HOME);

        assertFalse(awayBet.isWon());
        assertEquals(0, market.getTotalPayout().compareTo(BigDecimal.ZERO));
    }

    // Why this test guards the status check: it verifies that the guard throws BEFORE
    // touching bets. A version that asserted the exception but didn't verify
    // betRepository.never() would miss a bug where the guard throws after already mutating bets.
    @Test
    void throws_when_market_is_already_settled() {
        Market market = new Market("mkt-1", MarketStatus.SETTLED);
        Mockito.when(marketRepository.findById("mkt-1")).thenReturn(market);

        assertThrows(IllegalStateException.class,
                () -> service.settleMarket("mkt-1", BettingSide.HOME));

        Mockito.verify(betRepository, Mockito.never()).findByMarketId(Mockito.anyString());
    }
}
