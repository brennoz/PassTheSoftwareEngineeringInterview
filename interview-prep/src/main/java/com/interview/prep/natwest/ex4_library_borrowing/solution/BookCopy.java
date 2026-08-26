package com.interview.prep.natwest.ex4_library_borrowing.solution;

import java.util.concurrent.locks.ReentrantLock;

// Why availability lives HERE, not on Book: a library can hold several physical copies of
// the same title, and each copy is independently either on the shelf or out on loan. Book
// itself (isbn/title/author) never changes — putting mutable status on it would conflate
// "what the book is" with "where a specific copy currently is." One BookCopy per physical
// item is the model that makes "the last copy of this title" a real, testable thing.
//
// Why CopyStatus (enum), not a full GoF State pattern: see CopyStatus's own comment. Short
// version — two states, no per-state behavior beyond the label, so an enum is the right-sized
// tool. This class is where you'd introduce actual State-pattern classes later IF a status
// ever needed its own distinct borrow()/return() behavior; today it doesn't, so it isn't here.
public class BookCopy {

    private final String copyId;
    private final String isbn;
    private CopyStatus status = CopyStatus.AVAILABLE;

    // ── Part B (stretch) addition ──────────────────────────────────────────────────────
    // The core exercise (Part A) never needed this — a single-threaded borrowBook() can just
    // read `status`, check it, then set it BORROWED, as two separate statements, because
    // there's only ever one caller at a time. The lock exists ONLY because Part B's stretch
    // goal explicitly puts multiple threads through tryBorrow() concurrently — see
    // SolutionTest's concurrency test for why this specific race matters.
    private final ReentrantLock lock = new ReentrantLock();

    public BookCopy(String copyId, String isbn) {
        this.copyId = copyId;
        this.isbn = isbn;
    }

    public String getCopyId() {
        return copyId;
    }

    public String getIsbn() {
        return isbn;
    }

    // Part A would have been: "if (status == AVAILABLE) { status = BORROWED; return true; }
    // return false;" — correct for a single caller, but a classic check-then-act race for
    // multiple callers: two threads can both read status==AVAILABLE BEFORE either writes
    // status=BORROWED, and both "win." Part B's fix: fold the check AND the transition into
    // one atomic operation under the copy's own lock, so there is no window between "check"
    // and "act" for another thread to observe the stale state. Returns false (not an
    // exception) when already borrowed — the caller (Library) decides what "no copy
    // available" means, this class only reports fact.
    public boolean tryBorrow() {
        lock.lock();
        try {
            if (status != CopyStatus.AVAILABLE) {
                return false;
            }
            status = CopyStatus.BORROWED;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            status = CopyStatus.AVAILABLE;
        } finally {
            lock.unlock();
        }
    }

    // Read-only convenience for tests/prints — NOT used by borrow logic itself (that always
    // goes through tryBorrow's atomic check), so reading this value is never part of a race.
    public CopyStatus getStatus() {
        lock.lock();
        try {
            return status;
        } finally {
            lock.unlock();
        }
    }

    public boolean isAvailable() {
        return getStatus() == CopyStatus.AVAILABLE;
    }
}
