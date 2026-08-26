package com.interview.ood.q030_library_system.solution;

// Pattern: State — each BookCopy status is an object implementing this interface, not a field
// compared against an enum in a switch statement. The historical bug on this exact question
// (see answer/attempt1/) was BookCopy mutating its own state field directly instead of
// delegating to it — which meant the guards defined here (e.g. "you can't reserve a copy
// that's already borrowed") were dead code, never actually reached. The invariant this
// interface exists to enforce: ONLY a CopyState implementation may call
// BookCopy.setState(new X()) — BookCopy itself only ever delegates, never decides.
interface CopyState {
    void reserve(BookCopy copy);
    void checkout(BookCopy copy);
    void returnCopy(BookCopy copy);
    void markLost(BookCopy copy);
    String name();
}
