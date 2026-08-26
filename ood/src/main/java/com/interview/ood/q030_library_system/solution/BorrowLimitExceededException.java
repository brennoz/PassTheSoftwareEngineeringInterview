package com.interview.ood.q030_library_system.solution;

class BorrowLimitExceededException extends RuntimeException {
    BorrowLimitExceededException(String memberId) {
        super("Member " + memberId + " has reached the borrow limit");
    }
}
