package com.interview.pairingdrill.q077_market_settlement.answer;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MyAnswerTest {

    private final MarketRepository marketRepository = Mockito.mock(MarketRepository.class);
    private final BetRepository betRepository = Mockito.mock(BetRepository.class);

    private final MarketSettlementService service = new MarketSettlementService(marketRepository, betRepository);

    @Test
    void settles_winning_bets_when_market_is_open() {

        Market market = new Market("mkt-1", MarketStatus.OPEN);
        Bet homeBet = new Bet("bet-1", "mkt-1", BettingSide.HOME, BigDecimal.valueOf(1.75), new Stake(BigDecimal.valueOf(10), Currency.getInstance("GBP")));
        Bet awayBet = new Bet("bet-2", "mkt-1", BettingSide.AWAY, BigDecimal.valueOf(3.00), new Stake(BigDecimal.valueOf(5), Currency.getInstance("GBP")));

        Mockito.when(marketRepository.findById("mkt-1")).thenReturn(market);
        Mockito.when(betRepository.findByMarketId("mkt-1")).thenReturn(List.of(homeBet, awayBet));

        service.settleMarket("mkt-1", BettingSide.HOME);

        assertTrue(homeBet.isWon());
        assertFalse(awayBet.isWon());
        assertEquals(MarketStatus.SETTLED, market.getStatus());
    }

    @Test 
    void should_throw_illegal_argument_exception_when_market_not_opened_or_not_suspended () {
        
        Market market = new Market("mkt-1", MarketStatus.SETTLED);
        Mockito.when(marketRepository.findById("mkt-1")).thenReturn(market);
        assertThrows(IllegalStateException.class, () -> service.settleMarket("mkt-1", BettingSide.HOME));
    }
}
