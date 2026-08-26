package com.interview.ood.q030_library_system.solution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

class DailyFineStrategy implements FineCalculationStrategy {
    private final BigDecimal ratePerDay;

    DailyFineStrategy(BigDecimal ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    // ── TDD note ─────────────────────────────────────────────────────────────────────
    // Test 4 (return_on_time_produces_no_fine) only exercises the days<=0 branch. It was the
    // original test suite's blind spot: nothing ever exercised the days>0 branch — the
    // "fine actually gets calculated" path was never proven correct, only "no fine when not
    // overdue" was. Test 5 (return_after_due_date_produces_fine, new) closes that gap.
    @Override
    public BigDecimal calculate(Loan loan) {
        long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
        if (daysOverdue <= 0) {
            return BigDecimal.ZERO;
        }
        // BigDecimal.multiply, not double arithmetic — exact cents, no floating point drift.
        return ratePerDay.multiply(BigDecimal.valueOf(daysOverdue));
    }
}
