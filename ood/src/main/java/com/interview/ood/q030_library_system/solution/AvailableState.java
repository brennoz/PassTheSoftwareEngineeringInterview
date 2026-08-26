package com.interview.ood.q030_library_system.solution;

class AvailableState implements CopyState {
    @Override public void reserve(BookCopy c)    { c.setState(new ReservedState()); }
    @Override public void checkout(BookCopy c)   { c.setState(new BorrowedState()); }
    // Guard: an available copy has nothing to return or report lost. This is the exact
    // transition the historical bug never reached — reserving a copy that's already borrowed
    // used to silently succeed because BookCopy short-circuited past this guard entirely.
    @Override public void returnCopy(BookCopy c) { throw new IllegalStateException("Not borrowed"); }
    @Override public void markLost(BookCopy c)   { throw new IllegalStateException("Not borrowed"); }
    @Override public String name()               { return "AVAILABLE"; }
}
