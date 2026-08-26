package com.interview.prep.natwest.ex1_thread_safe_ledger.solution;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

// Each account owns its own lock (not one shared lock for the whole ledger) so unrelated
// accounts never contend with each other — a single global lock would serialize every
// operation across every customer, which doesn't scale past a handful of concurrent users.
public class Account {

    private final String id;
    private BigDecimal balance;
    private final ReentrantLock lock = new ReentrantLock();

    public Account(String id, BigDecimal openingBalance) {
        this.id = id;
        this.balance = openingBalance;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    ReentrantLock getLock() {
        return lock;
    }
}
