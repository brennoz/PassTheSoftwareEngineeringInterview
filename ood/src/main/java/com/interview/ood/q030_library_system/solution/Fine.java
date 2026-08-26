package com.interview.ood.q030_library_system.solution;

import java.math.BigDecimal;

// Why BigDecimal, never double/float: fine arithmetic must be exact — this course's
// standing rule for any monetary amount. Why LibraryService.returnBook() returns null
// instead of a Fine with amount ZERO when nothing is owed: see Test 4's TDD note on
// LibraryService — a null Fine is a cleaner signal to callers than a zero-amount Fine object
// they'd have to remember to check.
class Fine {
    private final Loan       loan;
    private final BigDecimal amount;
    private boolean          paid = false;

    Fine(Loan loan, BigDecimal amount) {
        this.loan = loan;
        this.amount = amount;
    }

    public void pay()             { this.paid = true; }
    public boolean isPaid()       { return paid; }
    public BigDecimal getAmount() { return amount; }
    public Loan getLoan()         { return loan; }
}
