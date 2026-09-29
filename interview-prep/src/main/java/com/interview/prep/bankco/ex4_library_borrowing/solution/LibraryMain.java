package com.interview.prep.bankco.ex4_library_borrowing.solution;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// Runnable demo — not a test. Part 1 shows the plain single-threaded happy path (Part A).
// Part 2 shows Part B's race: several members simultaneously trying to borrow the LAST copy
// of a book. Run it a few times — with BookCopy's lock in place, exactly one always wins.
// Comment out the locking in BookCopy.tryBorrow() (make it two separate statements instead
// of one atomic block) and re-run: you'll start seeing more than one "SUCCESS" printed.
public class LibraryMain {

    public static void main(String[] args) throws InterruptedException {
        InMemoryAuditTrail auditTrail = new InMemoryAuditTrail();
        Library library = new Library(auditTrail);

        Book book = new Book("978-0-13-468599-1", "Effective Java", "Joshua Bloch");
        library.addBook(book, 1); // only ONE copy — sets up Part 2's race deliberately
        library.registerMember(new Member("m1", "Alice"));

        System.out.println("=== Part 1: single-threaded happy path ===");
        BookCopy borrowed = library.borrowBook("m1", book.isbn());
        System.out.println("Alice borrowed: " + borrowed.getCopyId() + " (status: " + borrowed.getStatus() + ")");
        library.returnBook("m1", borrowed.getCopyId());
        System.out.println("Alice returned it. Status now: " + borrowed.getStatus());
        System.out.println("Audit trail: " + auditTrail.allEvents());

        System.out.println();
        System.out.println("=== Part 2: race for the (now available again) last copy ===");
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
                    System.out.println(memberId + ": SUCCESS");
                } catch (BookUnavailableException e) {
                    System.out.println(memberId + ": unavailable (expected for the losers)");
                } finally {
                    done.countDown();
                }
            });
        }

        done.await(5, TimeUnit.SECONDS);
        pool.shutdown();

        System.out.println("Total successful borrows out of " + racers + " racers: " + successCount.get()
                + " (expected exactly 1 — there was only one copy)");
    }
}
