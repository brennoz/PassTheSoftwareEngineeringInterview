package com.interview.algorithms.java_features.q061_highest_stake_per_market;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void should_filter_the_bet_with_highest_stake_per_market () {

        List<Bet> bets = List.of(
            bet("1", "mk-1", BigDecimal.valueOf(20)),
            bet("2", "mk-1", BigDecimal.valueOf(15)),
            bet("3", "mk-2", BigDecimal.valueOf(30)),
            bet("4", "mk-2", BigDecimal.valueOf(70)),
            bet("5", "mk-2", BigDecimal.valueOf(10)),
            bet("6", "mk-3", BigDecimal.valueOf(50)),
            bet("7", "mk-3", BigDecimal.valueOf(51)),
            bet("8", "mk-3", BigDecimal.valueOf(20))
        );

        Map<String, Bet> highestBetByMarketId = MyAnswer.highestStakePerMarket(bets);

        assertEquals(3, highestBetByMarketId.size());
        assertEquals(0, highestBetByMarketId.get("mk-1").stake().compareTo(BigDecimal.valueOf(20)));
        assertEquals(0, highestBetByMarketId.get("mk-2").stake().compareTo(BigDecimal.valueOf(70)));
        assertEquals(0, highestBetByMarketId.get("mk-3").stake().compareTo(BigDecimal.valueOf(51)));
    }

    private Bet bet(String id, String marketId, BigDecimal stake) {
        return new Bet(id, marketId, stake);
    }
}
