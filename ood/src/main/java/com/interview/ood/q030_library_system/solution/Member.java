package com.interview.ood.q030_library_system.solution;

import java.util.ArrayList;
import java.util.List;

// Why Member owns its own active-loan list rather than LibraryService scanning all loans to
// count how many belong to a member: information-expert — the object that HAS the data (its
// own loans) is the object that should answer questions about it (has it reached the limit),
// not a service class reaching into another object's internals to derive the same answer.
class Member {
    private static final int MAX_LOANS = 3;

    private final String     id;
    private final String     name;
    private final List<Loan> activeLoans = new ArrayList<>();

    Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // ── TDD note ─────────────────────────────────────────────────────────────────────
    // Test 2 (checkout_throws_when_borrow_limit_reached) is what puts MAX_LOANS at 3 and
    // this guard method on Member rather than as a raw size() check inline in
    // LibraryService — naming the rule (hasReachedBorrowLimit) makes the guard self-
    // documenting at the call site instead of a bare `activeLoans.size() >= 3`.
    public boolean hasReachedBorrowLimit() { return activeLoans.size() >= MAX_LOANS; }

    public void addActiveLoan(Loan loan)    { activeLoans.add(loan); }
    public void removeLoan(Loan loan)       { activeLoans.remove(loan); }
    public String getId()                   { return id; }
    public String getName()                 { return name; }
    public List<Loan> getActiveLoans()      { return List.copyOf(activeLoans); }
}
