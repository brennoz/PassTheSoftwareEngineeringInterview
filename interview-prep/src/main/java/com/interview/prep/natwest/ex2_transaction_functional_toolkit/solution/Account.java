package com.interview.prep.natwest.ex2_transaction_functional_toolkit.solution;

import java.math.BigDecimal;

public class Account {

    private final String id;
    private final BigDecimal balance;
    private final boolean active;

    public Account(String id, BigDecimal balance, boolean active) {
        this.id = id;
        this.balance = balance;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }
}
