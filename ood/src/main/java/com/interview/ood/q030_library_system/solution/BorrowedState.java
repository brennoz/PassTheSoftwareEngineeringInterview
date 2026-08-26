package com.interview.ood.q030_library_system.solution;

class BorrowedState implements CopyState {
    @Override public void reserve(BookCopy c)    { throw new IllegalStateException("Already borrowed"); }
    @Override public void checkout(BookCopy c)   { throw new IllegalStateException("Already borrowed"); }
    @Override public void returnCopy(BookCopy c) { c.setState(new AvailableState()); }
    @Override public void markLost(BookCopy c)   { c.setState(new LostState()); }
    @Override public String name()               { return "BORROWED"; }
}
