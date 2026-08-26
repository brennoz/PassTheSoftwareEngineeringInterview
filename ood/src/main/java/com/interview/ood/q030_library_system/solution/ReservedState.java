package com.interview.ood.q030_library_system.solution;

class ReservedState implements CopyState {
    @Override public void reserve(BookCopy c)    { throw new IllegalStateException("Already reserved"); }
    @Override public void checkout(BookCopy c)   { c.setState(new BorrowedState()); }
    @Override public void returnCopy(BookCopy c) { c.setState(new AvailableState()); }
    @Override public void markLost(BookCopy c)   { throw new IllegalStateException("Not borrowed"); }
    @Override public String name()               { return "RESERVED"; }
}
