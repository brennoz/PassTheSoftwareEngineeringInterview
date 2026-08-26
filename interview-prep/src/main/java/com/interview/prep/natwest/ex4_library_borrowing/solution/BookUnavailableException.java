package com.interview.prep.natwest.ex4_library_borrowing.solution;

// A checked exception here would force every caller to try/catch or declare throws for a
// condition that's routine, not exceptional infrastructure failure — unchecked (RuntimeException)
// matches how the rest of this course's domain exceptions are modelled (see
// InsufficientFundsException in Ex1), and keeps Library's method signatures clean.
public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String isbn) {
        super("No copies of book " + isbn + " are currently available");
    }
}
