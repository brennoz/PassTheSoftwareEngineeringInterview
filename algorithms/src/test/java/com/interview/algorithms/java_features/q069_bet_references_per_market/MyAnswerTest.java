package com.interview.algorithms.java_features.q069_bet_references_per_market;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

// Write your tests here.
// Cover:
//   - Multiple bets across multiple markets: grouped and sorted per market.
//   - A market with a single bet: single-element list.
//   - Empty input: empty map (not null).
public class MyAnswerTest {

    @Test
    void should_get_a_map_of_list_of_bet_ref_by_market () {

        List<MyAnswer.Bet> bets = List.of(
            bet("ref-8", "Roma Wins - Roma vs Atalanta", "bettor-1", BigDecimal.valueOf(30)),
            bet("ref-5", "Lazio Loses - Lazio vs Roma", "bettor-2", BigDecimal.valueOf(40)),
            bet("ref-1", "Roma Wins - Roma vs Atalanta", "bettor-3", BigDecimal.valueOf(50)),
            bet("ref-4", "Lazio Loses - Lazio vs Roma", "bettor-4", BigDecimal.valueOf(60)),
            bet("ref-7", "Roma Wins - Roma vs Atalanta", "bettor-5", BigDecimal.valueOf(70)),
            bet("ref-3", "Lazio Loses - Lazio vs Roma", "bettor-6", BigDecimal.valueOf(80))
        );

        Map<String, List<String>> result = MyAnswer.betRefsByMarket(bets);

        assertEquals("ref-1", result.get("Roma Wins - Roma vs Atalanta").get(0));
        assertEquals("ref-7", result.get("Roma Wins - Roma vs Atalanta").get(1));
        assertEquals("ref-3", result.get("Lazio Loses - Lazio vs Roma").get(0));
        assertEquals("ref-4", result.get("Lazio Loses - Lazio vs Roma").get(1));
    }

    private MyAnswer.Bet bet(String betRef, String marketId, String bettor, BigDecimal stake) {
        return new MyAnswer.Bet(betRef, marketId, bettor, stake);
    }
}
