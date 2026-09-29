package com.interview.prep.bankco.ex4_library_borrowing.solution;

// Enum, not a full GoF State pattern: two states (AVAILABLE/BORROWED), no per-state BEHAVIOR
// beyond the label itself — borrow/return do the same lock-then-flip regardless of which
// state you're leaving. A State hierarchy (AvailableState/BorrowedState, each implementing
// borrow()/returnCopy() differently) would be premature here — see BookCopy for the tradeoff.
// This enum is the seam that keeps the door open: a THIRD status (RESERVED, LOST, DAMAGED)
// is just a new case here, right up until one of them needs genuinely different behavior
// (e.g. RESERVED expiring after N days, LOST blocking return entirely) — that divergence in
// behavior, not the mere existence of more labels, is the actual trigger to graduate to State.
public enum CopyStatus {
    AVAILABLE,
    BORROWED
}
