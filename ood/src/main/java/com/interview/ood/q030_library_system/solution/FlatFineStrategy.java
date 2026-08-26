package com.interview.ood.q030_library_system.solution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

class FlatFineStrategy implements FineCalculationStrategy {
    private final BigDecimal flatAmount;

    FlatFineStrategy(BigDecimal flatAmount) {
        this.flatAmount = flatAmount;
    }

    @Override
    public BigDecimal calculate(Loan loan) {
        long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
        return daysOverdue > 0 ? flatAmount : BigDecimal.ZERO;
    }
}
