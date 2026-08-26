package com.interview.prep.natwest.ex4_library_borrowing.solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Pattern: this class is deliberately a thin ORCHESTRATOR, not a god-object holding every
// field. It coordinates BookCopy (availability + the Part B lock), Member (borrowed-copy
// state) and AuditTrail (side effect) — each of those owns its own state and its own
// invariant. Library's job is sequencing calls to them correctly, nothing more. That
// separation is exactly what let Part B's stretch goal bolt a lock onto BookCopy alone,
// without Library's borrowBook() method needing to change AT ALL.
public class Library {

    private final Map<String, Book> catalog = new HashMap<>();
    private final Map<String, List<BookCopy>> copiesByIsbn = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final AuditTrail auditTrail;

    // Constructor-injected AuditTrail — this is the seam the @Spy test in SolutionTest uses.
    // A Library that `new InMemoryAuditTrail()`'d internally would be untestable this way;
    // an interface passed in from outside is what makes swapping in a spied real instance
    // (or a mock, or a Kafka-backed implementation in production) possible without touching
    // this class.
    public Library(AuditTrail auditTrail) {
        this.auditTrail = auditTrail;
    }

    public void addBook(Book book, int copyCount) {
        catalog.put(book.isbn(), book);
        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < copyCount; i++) {
            copies.add(new BookCopy(book.isbn() + "-copy-" + i, book.isbn()));
        }
        copiesByIsbn.put(book.isbn(), copies);
    }

    public void registerMember(Member member) {
        members.put(member.getId(), member);
    }

    // Read-only query, delegating to Member (information-expert again — Library doesn't
    // maintain its own duplicate copy of "who borrowed what"). Exists mainly so tests and
    // callers can verify borrow/return state without Library exposing its internal Member
    // objects directly.
    public boolean hasBorrowed(String memberId, String copyId) {
        return requireMember(memberId).hasBorrowed(copyId);
    }

    // ── TDD note ──────────────────────────────────────────────────────────────────────
    // This is the FIRST method written, driven by the FIRST test: "borrowing a book with one
    // available copy succeeds and marks that copy unavailable." Writing that test first forces
    // the return type decision immediately — returning the BookCopy (not void, not a boolean)
    // because the caller (and Part B's race test) needs to know WHICH physical copy they got.
    public BookCopy borrowBook(String memberId, String isbn) {
        Member member = requireMember(memberId);
        List<BookCopy> copies = requireCopies(isbn);

        // Try every copy, not just copies.get(0): with several copies of the same title, a
        // caller shouldn't be blocked because copy #0 specifically is out — any available
        // copy will do. tryBorrow() is BookCopy's atomic check-and-flip (see BookCopy) — by
        // the time we're back here with `true`, that copy is already ours, no second check
        // needed.
        for (BookCopy copy : copies) {
            if (copy.tryBorrow()) {
                member.recordBorrow(copy.getCopyId());
                auditTrail.record(memberId + " borrowed " + copy.getCopyId());
                return copy;
            }
        }
        // Every copy.tryBorrow() returned false — none were available. This is the SECOND
        // test written: "borrowing when no copies are available throws." Written second, not
        // first, because it needs the happy-path's data model already in place to express
        // "zero of these copies is available" meaningfully.
        throw new BookUnavailableException(isbn);
    }

    // ── TDD note ──────────────────────────────────────────────────────────────────────
    // THIRD test: "returning a borrowed copy makes it available again and clears it from the
    // member's record." Written after borrow works, because return only makes sense to test
    // once there's a way to get a copy INTO the borrowed state to begin with — you can't
    // meaningfully return a book you can't yet borrow.
    public void returnBook(String memberId, String copyId) {
        Member member = requireMember(memberId);
        if (!member.hasBorrowed(copyId)) {
            // FOURTH test: returning a copy you never borrowed throws, rather than silently
            // freeing another member's book — the "real use / user journey" case the NatWest
            // feedback specifically calls out as often missed.
            throw new CopyNotBorrowedByMemberException(memberId, copyId);
        }
        BookCopy copy = findCopy(copyId);
        copy.release();
        member.recordReturn(copyId);
        auditTrail.record(memberId + " returned " + copyId);
    }

    private Member requireMember(String memberId) {
        Member member = members.get(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Unknown member: " + memberId);
        }
        return member;
    }

    private List<BookCopy> requireCopies(String isbn) {
        List<BookCopy> copies = copiesByIsbn.get(isbn);
        if (copies == null) {
            throw new IllegalArgumentException("Unknown book: " + isbn);
        }
        return copies;
    }

    private BookCopy findCopy(String copyId) {
        return copiesByIsbn.values().stream()
                .flatMap(List::stream)
                .filter(c -> c.getCopyId().equals(copyId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown copy: " + copyId));
    }
}
