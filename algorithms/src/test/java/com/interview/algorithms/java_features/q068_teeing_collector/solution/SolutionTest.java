package com.interview.algorithms.java_features.q068_teeing_collector.solution;

import com.interview.algorithms.java_features.q068_teeing_collector.MyAnswer.Settlement;
import com.interview.algorithms.java_features.q068_teeing_collector.MyAnswer.SettlementReport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolutionTest {

    @Test
    void builds_report_with_mixed_settlements() {
        List<Settlement> settlements = List.of(
                new Settlement("b1", new BigDecimal("250.00"), true),
                new Settlement("b2", new BigDecimal("100.00"), false),
                new Settlement("b3", new BigDecimal("75.50"), true),
                new Settlement("b4", new BigDecimal("30.00"), false)
        );

        SettlementReport report = Solution.buildReport(settlements);

        // totalWinnerPayout: 250.00 + 75.50 = 325.50 (won bets only)
        assertEquals(0, report.totalWinnerPayout().compareTo(new BigDecimal("325.50")));
        // settledCount: ALL 4 bets processed regardless of outcome
        assertEquals(4L, report.settledCount());
    }

    @Test
    void all_lost_gives_zero_payout_but_correct_total_count() {
        // This test isolates the pre-filter trap: pre-filtering to won-only would give
        // settledCount = 0 here instead of 3. The correct implementation must not
        // filter the stream before teeing.
        List<Settlement> settlements = List.of(
                new Settlement("b1", new BigDecimal("50.00"), false),
                new Settlement("b2", new BigDecimal("80.00"), false),
                new Settlement("b3", new BigDecimal("120.00"), false)
        );

        SettlementReport report = Solution.buildReport(settlements);

        assertEquals(0, report.totalWinnerPayout().compareTo(BigDecimal.ZERO));
        assertEquals(3L, report.settledCount());
    }

    @Test
    void empty_list_gives_zero_defaults() {
        SettlementReport report = Solution.buildReport(List.of());

        assertEquals(0, report.totalWinnerPayout().compareTo(BigDecimal.ZERO));
        assertEquals(0L, report.settledCount());
    }
}
