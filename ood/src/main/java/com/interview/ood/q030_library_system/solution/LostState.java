package com.interview.ood.q030_library_system.solution;

// Terminal state — every transition throws, including markLost() itself (can't lose an
// already-lost copy). No outbound edge in the state graph; a lost copy stays lost.
class LostState implements CopyState {
    @Override public void reserve(BookCopy c)    { throw new IllegalStateException("Copy is lost"); }
    @Override public void checkout(BookCopy c)   { throw new IllegalStateException("Copy is lost"); }
    @Override public void returnCopy(BookCopy c) { throw new IllegalStateException("Copy is lost"); }
    @Override public void markLost(BookCopy c)   { throw new IllegalStateException("Already lost"); }
    @Override public String name()               { return "LOST"; }
}
