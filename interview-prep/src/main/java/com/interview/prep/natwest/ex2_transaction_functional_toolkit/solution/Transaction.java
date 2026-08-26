package com.interview.prep.natwest.ex2_transaction_functional_toolkit.solution;

import java.math.BigDecimal;

public class Transaction {

    private final String accountId;
    private final BigDecimal amount;

    public Transaction(String accountId, BigDecimal amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
