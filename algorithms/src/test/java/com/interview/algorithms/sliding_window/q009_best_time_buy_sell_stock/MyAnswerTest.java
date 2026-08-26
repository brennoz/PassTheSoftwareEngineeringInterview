package com.interview.algorithms.sliding_window.q009_best_time_buy_sell_stock;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MyAnswerTest {

    private final MyAnswer solution = new MyAnswer();

    @Test
    void returnsMaxProfit() {
        assertThat(solution.maxProfit(new int[]{7,1,5,3,6,4})).isEqualTo(5);
    }

    @Test
    void returnsZeroWhenNoProfitPossible() {
        assertThat(solution.maxProfit(new int[]{7,6,4,3,1})).isEqualTo(0);
    }
}
