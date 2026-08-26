package com.interview.ood.q030_library_system.solution;

// LibraryService in this exercise never explicitly transitions a copy INTO OverdueState —
// overdueness is derived from Loan.isOverdue() (dueDate vs today), not tracked as a copy
// transition. This state exists for completeness of the model (a real system would have a
// scheduled job walk loans and transition copies), same guards as BorrowedState otherwise.
class OverdueState implements CopyState {
    @Override public void reserve(BookCopy c)    { throw new IllegalStateException("Copy is overdue"); }
    @Override public void checkout(BookCopy c)   { throw new IllegalStateException("Copy is overdue"); }
    @Override public void returnCopy(BookCopy c) { c.setState(new AvailableState()); }
    @Override public void markLost(BookCopy c)   { c.setState(new LostState()); }
    @Override public String name()               { return "OVERDUE"; }
}
