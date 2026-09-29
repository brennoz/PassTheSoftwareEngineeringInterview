package com.interview.algorithms.java_features.q068_teeing_collector.solution;

import com.interview.algorithms.java_features.q068_teeing_collector.MyAnswer.Settlement;
import com.interview.algorithms.java_features.q068_teeing_collector.MyAnswer.SettlementReport;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

    // Collectors.teeing (Java 12): feeds every stream element into two independent downstream
    // collectors in a single pass, then merges their results with a BiFunction.
    //
    // Collectors.filtering (Java 9): applies a Predicate inside a Collector pipeline.
    // It is used here because the two aggregations need DIFFERENT filtering:
    //   downstream1 — totalWinnerPayout: won settlements only  → Collectors.filtering
    //   downstream2 — settledCount: ALL settlements           → no filter, plain counting()
    //
    // The pre-filter trap: .filter(Settlement::won) before .collect(teeing(...)) would
    // strip all non-winners before counting() sees them, so settledCount would equal the
    // number of winners — wrong. Collectors.filtering scopes the predicate to downstream1
    // only; downstream2 still sees every element.
    //
    // reducing(identity, mapper, combiner) — the 3-arg form — returns BigDecimal directly.
    // The 1-arg form, reducing(BinaryOperator), returns Optional<BigDecimal>.
    // SettlementReport's constructor expects BigDecimal, so the 1-arg form would fail to
    // compile here. The identity BigDecimal.ZERO also seeds an empty won-partition to ZERO
    // instead of null.
    //
    // The BiFunction merger is satisfied by the constructor reference SettlementReport::new
    // because SettlementReport(BigDecimal, long) matches BiFunction<BigDecimal, Long, SettlementReport>.
    public static SettlementReport buildReport(List<Settlement> settlements) {
        return settlements.stream()
                .collect(Collectors.teeing(
                        Collectors.filtering(Settlement::won,
                                Collectors.reducing(BigDecimal.ZERO, Settlement::payout, BigDecimal::add)),
                        Collectors.counting(),
                        SettlementReport::new
                ));
    }
}
