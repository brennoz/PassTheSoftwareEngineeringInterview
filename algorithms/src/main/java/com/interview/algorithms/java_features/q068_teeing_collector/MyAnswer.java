package com.interview.algorithms.java_features.q068_teeing_collector;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class MyAnswer {

    public record Settlement(String betId, BigDecimal payout, boolean won) {}
    public record SettlementReport(BigDecimal totalWinnerPayout, long settledCount) {}

    public static SettlementReport buildReport(List<Settlement> settlements) {

        return settlements.stream()
                .collect(
                    Collectors.teeing(
                        Collectors.filtering(settlement -> settlement.won(), 
                                            Collectors.reducing(BigDecimal.ZERO, Settlement::payout, BigDecimal::add))
                        , Collectors.counting(), 
                        SettlementReport::new)
                );
    }
}
