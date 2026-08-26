package com.interview.prep.natwest.ex1_thread_safe_ledger.solution;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String accountId) {
        super("Insufficient funds in account " + accountId);
    }
}
