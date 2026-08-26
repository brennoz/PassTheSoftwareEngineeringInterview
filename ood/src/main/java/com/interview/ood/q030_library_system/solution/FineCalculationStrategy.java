package com.interview.ood.q030_library_system.solution;

import java.math.BigDecimal;

// Pattern: Strategy — the fine formula is injected into LibraryService, which never knows or
// cares which concrete strategy is active. Adding a THIRD fine policy (e.g. a capped daily
// rate) means adding a new class here, never touching LibraryService — same Open/Closed
// benefit CopyState gives the status transitions.
interface FineCalculationStrategy {
    BigDecimal calculate(Loan loan);
}
