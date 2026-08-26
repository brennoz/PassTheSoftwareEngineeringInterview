package com.interview.ood.q030_library_system.solution;

import java.time.LocalDate;
import java.util.UUID;

// A Loan is created once, by LibraryService.checkout(), and mutated exactly once after that
// (returnedOn set on return) — everything else is immutable. dueDate is computed at
// construction from loanDays, not stored as a separate mutable field the caller could drift
// out of sync with borrowedOn.
class Loan {
    private final String    id;
    private final Member    member;
    private final BookCopy  copy;
    private final LocalDate borrowedOn;
    private final LocalDate dueDate;
    private LocalDate       returnedOn;

    Loan(Member member, BookCopy copy, int loanDays) {
        this.id = UUID.randomUUID().toString();
        this.member = member;
        this.copy = copy;
        this.borrowedOn = LocalDate.now();
        this.dueDate = borrowedOn.plusDays(loanDays);
    }

    public boolean isOverdue() {
        return returnedOn == null && LocalDate.now().isAfter(dueDate);
    }

    // Package-private — only LibraryService.returnBook() should ever set this, as part of
    // the single returnBook() orchestration step, not as a standalone public mutation.
    void setReturnedOn(LocalDate date) { this.returnedOn = date; }

    public LocalDate getDueDate() { return dueDate; }
    public Member    getMember()  { return member; }
    public BookCopy  getCopy()    { return copy; }
    public String    getId()      { return id; }
}
