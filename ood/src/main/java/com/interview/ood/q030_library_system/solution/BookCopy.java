package com.interview.ood.q030_library_system.solution;

// ── TDD note ─────────────────────────────────────────────────────────────────────────
// Test 1 (checkout_changes_copy_status_to_borrowed) drives this class's whole shape: it
// needs getStatus() to observe the transition, which forced the decision to expose status
// as a String via CopyState.name() rather than exposing the CopyState object itself —
// callers should never need to know CopyState exists, only what the current status IS.
class BookCopy {
    private final String barcode;
    private final Book   book;
    // State held as an object, not an enum — see CopyState. Starts AVAILABLE; every other
    // state is reached only through a guarded transition.
    private CopyState state = new AvailableState();

    BookCopy(String barcode, Book book) {
        this.barcode = barcode;
        this.book = book;
    }

    // Pure delegation — NO logic here. This is the exact line the historical bug got wrong:
    // writing `state = new ReservedState()` directly in this method instead of delegating to
    // `state.reserve(this)` skips every guard defined in CopyState's implementations. If you
    // ever see `new` inside one of these four methods, delegation is broken.
    public void reserve()    { state.reserve(this); }
    public void checkout()   { state.checkout(this); }
    public void returnCopy() { state.returnCopy(this); }
    public void markLost()   { state.markLost(this); }

    // Package-private on purpose: only CopyState implementations should ever call this — a
    // public setState would let LibraryService or a test bypass the guards entirely.
    void setState(CopyState s) { this.state = s; }

    public String getStatus()  { return state.name(); }
    public String getBarcode() { return barcode; }
    public Book   getBook()    { return book; }
}
