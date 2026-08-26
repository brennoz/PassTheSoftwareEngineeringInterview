package com.interview.ood.q030_library_system.solution;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

// Restructured from the original flat MyAnswerTest — see answer/attempt1/ for the historical
// version this was graded against (2/4 passing on first review, since fixed). Same four test
// names/scenarios kept as the TDD baseline; Test 5 is new — the original suite never actually
// exercised the "a fine gets produced" path, only "no fine when on time."
class SolutionTest {

    private Book    book()   { 
        return new Book("978-0", "Clean Code", "Martin"); 
    }

    private BookCopy copy()   { 
        return new BookCopy("BC-001", book()); 
    }
    
    private Member   member() { 
        return new Member("m1", "Alice"); 
    }

    // ── Test 1 (written first) ──────────────────────────────────────────────────────
    // Simplest possible case. Forces LibraryService.checkout() to return a Loan (not void)
    // and BookCopy to expose getStatus() as a String — see BookCopy's TDD note.
    @Test
    void checkout_changes_copy_status_to_borrowed() {
        LibraryService service = new LibraryService(new FlatFineStrategy(new BigDecimal("5.00")));
        Member alice = member();
        BookCopy copy = copy();

        service.checkout(alice, copy, 14);

        assertEquals("BORROWED", copy.getStatus());
        assertEquals(1, alice.getActiveLoans().size());
    }

    // ── Test 2 (written second) ─────────────────────────────────────────────────────
    // Depends on Test 1's checkout() already working — three successful checkouts set up
    // the precondition before the fourth is expected to fail.
    @Test
    void checkout_throws_when_borrow_limit_reached() {
        LibraryService service = new LibraryService(new FlatFineStrategy(new BigDecimal("5.00")));
        Member alice = member();
        Book book = book();

        service.checkout(alice, new BookCopy("BC-1", book), 14);
        service.checkout(alice, new BookCopy("BC-2", book), 14);
        service.checkout(alice, new BookCopy("BC-3", book), 14);

        BookCopy fourth = new BookCopy("BC-4", book);
        assertThrows(BorrowLimitExceededException.class,
                () -> service.checkout(alice, fourth, 14));
    }

    // ── Test 3 (written third) ──────────────────────────────────────────────────────
    // Proves the State pattern's guards actually fire — this is the test the historical
    // bug (attempt1) failed, because BookCopy mutated its state field directly instead of
    // delegating to it, so AvailableState.returnCopy()'s throw was never reached.
    @Test
    void invalid_state_transition_throws() {
        BookCopy copy = copy();

        assertThrows(IllegalStateException.class, copy::returnCopy);
    }

    // ── Test 4 (written fourth) ─────────────────────────────────────────────────────
    // The round trip — only meaningful once checkout works, since you need a borrowed copy
    // to return. This is also where the historical BigDecimal `==` bug lived: the fine
    // amount happened to compare equal to the static BigDecimal.ZERO by reference in this
    // exact scenario, masking the bug until a strategy returned a different Zero instance.
    @Test
    void return_on_time_produces_no_fine() {
        LibraryService service = new LibraryService(new DailyFineStrategy(new BigDecimal("0.50")));
        Member alice = member();
        BookCopy copy = copy();

        Loan loan = service.checkout(alice, copy, 14); // due in the future
        Fine fine = service.returnBook(alice, loan);

        assertNull(fine);
        assertEquals("AVAILABLE", copy.getStatus());
        assertTrue(alice.getActiveLoans().isEmpty());
    }

    // ── Test 5 (written fifth — new, closes the original suite's gap) ──────────────
    // Negative loanDays backdates the due date so the loan is overdue the instant it's
    // created — the simplest way to set up "already overdue" without faking the system
    // clock or adding test-only constructors to Loan.
    @Test
    void return_after_due_date_produces_fine() {
        LibraryService service = new LibraryService(new DailyFineStrategy(new BigDecimal("0.50")));
        Member alice = member();
        BookCopy copy = copy();

        Loan loan = service.checkout(alice, copy, -5); // due 5 days ago
        Fine fine = service.returnBook(alice, loan);

        assertEquals(0, new BigDecimal("2.50").compareTo(fine.getAmount()));
    }
}
