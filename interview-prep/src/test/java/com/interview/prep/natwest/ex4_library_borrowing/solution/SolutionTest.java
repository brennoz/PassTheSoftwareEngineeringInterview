package com.interview.prep.natwest.ex4_library_borrowing.solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

// @ExtendWith(MockitoExtension.class): registers Mockito's JUnit 5 hook so @Spy fields below
// are initialised automatically before each test — same extension mechanism used for @Mock
// throughout this course (see this repo's round-1 cheatsheet on @ExtendWith).
@ExtendWith(MockitoExtension.class)
class SolutionTest {

    // @Spy, not @Mock: we want the REAL InMemoryAuditTrail behind this field — real storage,
    // real allEvents() — while ALSO being able to verify(...) that record() was called. A
    // @Mock here would return null/no-op for every method unless individually stubbed, which
    // would make the "audit trail actually contains the event" half of the test impossible
    // without manually stubbing record() to fake the storage — defeating the point of testing
    // against real behavior.
    @Spy
    private InMemoryAuditTrail auditTrail = new InMemoryAuditTrail();

    private Library library;
    private Book book;

    @BeforeEach
    void setUp() {
        library = new Library(auditTrail);
        book = new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch");
        library.registerMember(new Member("m1", "Alice"));
    }

    // ── Test 1 (written first) ──────────────────────────────────────────────────────────
    // The simplest possible case: one book, one copy, one borrower. This test alone forces
    // every core design decision: Library needs addBook/registerMember/borrowBook, and
    // borrowBook must return something identifying WHICH copy was lent (not void) so the
    // test — and later, returnBook — has something to act on.
    @Test
    void borrowingAvailableCopyMarksItUnavailable() {
        library.addBook(book, 1);

        BookCopy copy = library.borrowBook("m1", book.isbn());

        assertEquals(CopyStatus.BORROWED, copy.getStatus());
    }

    // ── Test 2 (written second) ─────────────────────────────────────────────────────────
    // Depends on Test 1's model already existing — "no copies available" only means something
    // once "a copy can become unavailable" is implemented. Zero copies is the simplest way to
    // set this precondition up without needing a second member to borrow the only copy first.
    @Test
    void borrowingWithNoAvailableCopiesThrows() {
        library.addBook(book, 0);

        assertThrows(BookUnavailableException.class, () -> library.borrowBook("m1", book.isbn()));
    }

    // ── Test 3 (written third) ──────────────────────────────────────────────────────────
    // The round trip. Only meaningful after borrow works — you need a borrowed copy to return.
    @Test
    void returningBorrowedCopyMakesItAvailableAgain() {
        library.addBook(book, 1);
        BookCopy copy = library.borrowBook("m1", book.isbn());

        library.returnBook("m1", copy.getCopyId());

        assertEquals(CopyStatus.AVAILABLE, copy.getStatus());
        assertFalse(library.hasBorrowed("m1", copy.getCopyId()));
    }

    // ── Test 4 (written fourth) ──────────────────────────────────────────────────────────
    // The "real use" case the NatWest feedback specifically calls out: returning something
    // you never borrowed must fail loudly, not silently free another member's book.
    @Test
    void returningACopyNeverBorrowedThrows() {
        library.addBook(book, 1);
        BookCopy copy = library.borrowBook("m1", book.isbn());
        library.registerMember(new Member("m2", "Bob"));

        assertThrows(CopyNotBorrowedByMemberException.class,
                () -> library.returnBook("m2", copy.getCopyId()));
    }

    // ── Test 5 (written fifth — the spy) ────────────────────────────────────────────────
    // Layered on AFTER the core flow is proven correct — auditing a broken borrow flow would
    // be testing the wrong thing. verify() checks the INTERACTION happened; allEvents() (via
    // the same spied real object) checks the RESULT of that interaction is actually correct —
    // two different kinds of assertion Mockito's spy lets us make on one collaborator.
    @Test
    void borrowingRecordsAnAuditEvent() {
        library.addBook(book, 1);

        library.borrowBook("m1", book.isbn());

        verify(auditTrail).record("m1 borrowed 978-0-13-468599-1-copy-0");
        assertTrue(auditTrail.allEvents().contains("m1 borrowed 978-0-13-468599-1-copy-0"));
    }

    // ── Test 6 (Part B — stretch, written last) ─────────────────────────────────────────
    // Only makes sense once single-threaded correctness (Tests 1-5) is solid — a concurrency
    // test on top of broken sequential logic proves nothing. 10 members race for the ONE
    // copy; BookCopy.tryBorrow()'s lock means the check-and-flip is atomic, so exactly one
    // can win regardless of thread interleaving. Against an unlocked/naive BookCopy this test
    // is flaky (occasionally 2+ "successes"), not reliably red — which is itself worth saying
    // out loud live: race-condition bugs often don't fail every run, that's what makes them
    // dangerous in production.
    @Test
    void exactlyOneConcurrentBorrowerWinsTheLastCopy() throws InterruptedException {
        library.addBook(book, 1);
        int racers = 10;
        for (int i = 0; i < racers; i++) {
            library.registerMember(new Member("racer-" + i, "Racer " + i));
        }

        ExecutorService pool = Executors.newFixedThreadPool(racers);
        CountDownLatch done = new CountDownLatch(racers);
        AtomicInteger successCount = new AtomicInteger();

        for (int i = 0; i < racers; i++) {
            String memberId = "racer-" + i;
            pool.submit(() -> {
                try {
                    library.borrowBook(memberId, book.isbn());
                    successCount.incrementAndGet();
                } catch (BookUnavailableException expected) {
                    // expected for every racer except the winner
                } finally {
                    done.countDown();
                }
            });
        }

        assertTrue(done.await(5, TimeUnit.SECONDS));
        pool.shutdown();

        assertEquals(1, successCount.get());
    }
}
