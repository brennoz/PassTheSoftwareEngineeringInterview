package com.interview.ood.q030_library_system.solution;

import java.math.BigDecimal;

// Runnable demo — not a test. Walks the same "user journey" a live interviewer would want
// talked through: register → checkout → (attempt to over-borrow) → attempt an invalid
// transition → return on time (no fine) → return late (fine charged) — one continuous story,
// not five disconnected method calls.
public class LibraryMain {

    public static void main(String[] args) {
        LibraryService service = new LibraryService(new DailyFineStrategy(new BigDecimal("0.50")));

        Book book = new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch");
        Member alice = new Member("m1", "Alice");

        System.out.println("=== Happy path: checkout then on-time return ===");
        BookCopy copy1 = new BookCopy("copy-1", book);
        Loan loan1 = service.checkout(alice, copy1, 14);
        System.out.println("Checked out " + copy1.getBarcode() + " — status: " + copy1.getStatus());
        Fine fine1 = service.returnBook(alice, loan1);
        System.out.println("Returned on time. Status: " + copy1.getStatus() + ", fine: " + fine1);

        System.out.println();
        System.out.println("=== Invalid transition: returning a copy that's already available ===");
        try {
            copy1.returnCopy();
        } catch (IllegalStateException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Borrow limit: 3 is the max ===");
        Member bob = new Member("m2", "Bob");
        for (int i = 0; i < 3; i++) {
            service.checkout(bob, new BookCopy("bob-copy-" + i, book), 14);
        }
        try {
            service.checkout(bob, new BookCopy("bob-copy-4", book), 14);
        } catch (BorrowLimitExceededException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== Overdue return: negative loanDays backdates the due date ===");
        BookCopy copy2 = new BookCopy("copy-2", book);
        Loan overdueLoan = service.checkout(alice, copy2, -5); // due 5 days ago, immediately
        Fine fine2 = service.returnBook(alice, overdueLoan);
        System.out.println("Returned 5 days late. Fine: " + (fine2 != null ? fine2.getAmount() : "none")
                + " (expected 2.50 = 0.50/day x 5 days)");
    }
}
