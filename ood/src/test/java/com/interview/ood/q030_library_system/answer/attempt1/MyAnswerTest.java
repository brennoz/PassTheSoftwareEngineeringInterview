package com.interview.ood.q030_library_system.answer.attempt1;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class MyAnswerTest {

    private Book     book()   { return new Book("978-0", "Clean Code", "Martin"); }
    private BookCopy copy()   { return new BookCopy("BC-001", book()); }
    private Member   member() { return new Member("M-1", "Alice"); }

    @Test
    void checkout_changes_copy_status_to_borrowed() {
        LibraryService svc    = new LibraryService(new FlatFineStrategy(new BigDecimal("5.00")));
        Member         alice  = member();
        BookCopy       copy   = copy();

        svc.checkout(alice, copy, 14);

        assertEquals("BORROWED", copy.getStatus());
        assertEquals(1, alice.getActiveLoans().size());
    }

    @Test
    void checkout_throws_when_borrow_limit_reached() {
        LibraryService svc   = new LibraryService(new FlatFineStrategy(new BigDecimal("5.00")));
        Member         alice = member();
        Book           book  = book();

        svc.checkout(alice, new BookCopy("BC-1", book), 14);
        svc.checkout(alice, new BookCopy("BC-2", book), 14);
        svc.checkout(alice, new BookCopy("BC-3", book), 14);

        BookCopy fourth = new BookCopy("BC-4", book);
        assertThrows(BorrowLimitExceededException.class,
                () -> svc.checkout(alice, fourth, 14));
    }

    @Test
    void return_on_time_produces_no_fine() {
        LibraryService svc   = new LibraryService(new DailyFineStrategy(new BigDecimal("0.50")));
        Member         alice = member();
        BookCopy       copy  = copy();

        Loan loan = svc.checkout(alice, copy, 14);   // due in future
        Fine fine = svc.returnBook(alice, loan);

        assertNull(fine);
        assertEquals("AVAILABLE", copy.getStatus());
        assertTrue(alice.getActiveLoans().isEmpty());
    }

    @Test
    void invalid_state_transition_throws() {
        BookCopy copy = copy();
        // Can't return a copy that is AVAILABLE
        assertThrows(IllegalStateException.class, copy::returnCopy);
    }
}
