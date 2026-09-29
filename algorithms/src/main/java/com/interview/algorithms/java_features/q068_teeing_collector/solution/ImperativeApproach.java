package com.interview.algorithms.java_features.q068_teeing_collector.solution;

import com.interview.algorithms.java_features.q068_teeing_collector.MyAnswer.Settlement;
import com.interview.algorithms.java_features.q068_teeing_collector.MyAnswer.SettlementReport;

import java.math.BigDecimal;
import java.util.List;

public class ImperativeApproach {

    // Two accumulators, one loop — the direct translation of what teeing expresses declaratively.
    // The iteration logic and the aggregation logic are tangled together; in teeing they are
    // expressed as two separate, composable Collector expressions.
    //
    // What the functional version buys you beyond this:
    //   Composability: teeing returns a Collector, so it can be nested inside another Collector,
    //   e.g. groupingBy(market, teeing(...)) gives per-market reports in one pass.
    //   A for-each loop cannot be dropped into a downstream position like that.
    //
    //   Parallel readiness: a Collector has a combiner step that lets the JVM split the list,
    //   run accumulators in parallel threads, and merge partial results. The for-each has no
    //   combiner — parallelising it requires manual ThreadLocal or synchronized blocks.
    public static SettlementReport buildReport(List<Settlement> settlements) {
        BigDecimal totalWinnerPayout = BigDecimal.ZERO;
        long settledCount = 0L;

        for (Settlement s : settlements) {
            settledCount++;
            if (s.won()) {
                totalWinnerPayout = totalWinnerPayout.add(s.payout());
            }
        }

        return new SettlementReport(totalWinnerPayout, settledCount);
    }
}
