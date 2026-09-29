package com.interview.algorithms.java_features.q068_teeing_collector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    private MyAnswer myAnswer;

    @BeforeEach
    void init() {
        myAnswer = new MyAnswer();
    }

    // Write your tests here (TDD: red first, then implement MyAnswer.buildReport)
    @Test
    void should_build_a_report_with_tot_payout_to_winners_and_total_payout () {

        List<MyAnswer.Settlement> settlements = List.of(
            settlement("1", BigDecimal.valueOf(100), false),
            settlement("2", BigDecimal.valueOf(450), true),
            settlement("3", BigDecimal.valueOf(450), true),
            settlement("4", BigDecimal.valueOf(200), false),
            settlement("5", BigDecimal.valueOf(300), false),    
            settlement("6", BigDecimal.valueOf(600), true)
        );

        MyAnswer.SettlementReport report = myAnswer.buildReport(settlements);

        assertEquals(0, report.totalWinnerPayout().compareTo(BigDecimal.valueOf(1500)));
        assertEquals(6, report.settledCount());
    }

    private MyAnswer.Settlement settlement(String betId, BigDecimal payout, boolean won) {
        return new MyAnswer.Settlement(betId, payout, won);
    }
}
