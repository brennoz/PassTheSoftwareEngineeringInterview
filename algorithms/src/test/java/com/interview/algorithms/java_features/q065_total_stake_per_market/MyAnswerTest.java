package com.interview.algorithms.java_features.q065_total_stake_per_market;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void should_generate_a_per_order_market_stakes_map () {

        List<Bet> bets = List.of(
            bet("1", "mkt-3", BigDecimal.valueOf(20)),
            bet("1", "mkt-2", BigDecimal.valueOf(10)),
            bet("1", "mkt-1", BigDecimal.valueOf(45)),
            bet("1", "mkt-3", BigDecimal.valueOf(15)),
            bet("1", "mkt-1", BigDecimal.valueOf(40)),
            bet("1", "mkt-2", BigDecimal.valueOf(20)),
            bet("1", "mkt-3", BigDecimal.valueOf(35))
        );

        Map<String, BigDecimal> stakesPerMarket = MyAnswer.totalStakePerMarketSorted(bets);

        assertEquals(0, stakesPerMarket.get("mkt-1").compareTo(BigDecimal.valueOf(85)));
    }

    private Bet bet(String id, String marketId, BigDecimal stake) {
        return new Bet(id, marketId, stake);
    }
}
