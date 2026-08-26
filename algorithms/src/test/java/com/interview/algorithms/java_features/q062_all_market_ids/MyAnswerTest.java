package com.interview.algorithms.java_features.q062_all_market_ids;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void should_get_a_list_of_market_ids_distinct_and_sorted_from_all_bet_slips() {
        BetSlip betSlip = new BetSlip("1", List.of(
            sel("mk-2", "Milan"),
            sel("mk-1", "Roma"),
            sel("mk-3", "Torino"),
            sel("mk-1", "Roma")
        ));

        BetSlip betSlip2 = new BetSlip("2", List.of(
            sel("mk-4", "Cagliari"),
            sel("mk-2", "Milan"),
            sel("mk-1", "Roma"),
            sel("mk-5", "Fiorentina")
        ));   
        
        List<String> marketIds = MyAnswer.allMarketIdsSortedDistinct(List.of(betSlip, betSlip2));

        assertEquals(5, marketIds.size());
        assertEquals("mk-1", marketIds.get(0));
        assertEquals("mk-4", marketIds.get(3));
    }

    private Selection sel(String marketId, String outcome) {
        return new Selection(marketId, outcome);
    }
}
