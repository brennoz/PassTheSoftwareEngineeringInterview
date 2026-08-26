package com.interview.prep.natwest.ex4_library_borrowing.solution;

// Guards against returning a copy you never borrowed (typo'd copy id, or someone else's
// copy) — a real "user journey" edge case, not a validation nitpick: without this check
// returnBook() would silently free up a copy on another member's behalf.
public class CopyNotBorrowedByMemberException extends RuntimeException {
    public CopyNotBorrowedByMemberException(String memberId, String copyId) {
        super("Member " + memberId + " has not borrowed copy " + copyId);
    }
}
