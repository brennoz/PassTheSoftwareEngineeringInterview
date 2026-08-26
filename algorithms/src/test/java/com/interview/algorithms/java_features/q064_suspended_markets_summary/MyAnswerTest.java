package com.interview.algorithms.java_features.q064_suspended_markets_summary;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void should_generate_a_human_readable_string_with_all_suspended_markets_ordered () {
        List<Market> markets = List.of(
            mkt("match_301", MarketStatus.ACTIVE),
            mkt("match_101", MarketStatus.ACTIVE),
            mkt("match_201", MarketStatus.SUSPENDED),
            mkt("match_203", MarketStatus.ACTIVE),
            mkt("match_102", MarketStatus.ACTIVE),
            mkt("match_202", MarketStatus.ACTIVE),
            mkt("match_303", MarketStatus.SUSPENDED),
            mkt("match_302", MarketStatus.CLOSED)
        );

        String notSuspendedMarkets = MyAnswer.suspendedMarketsSummary(markets);

        assertEquals("[match_101, match_102, match_202, match_203, match_301, match_302]", notSuspendedMarkets);
    }

    private Market mkt(String marketId, MarketStatus status) {
        return new  Market(marketId, status);
    }

}
