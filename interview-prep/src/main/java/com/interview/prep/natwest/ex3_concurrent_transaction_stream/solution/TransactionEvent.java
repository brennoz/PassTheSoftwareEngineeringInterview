package com.interview.prep.natwest.ex3_concurrent_transaction_stream.solution;

import java.math.BigDecimal;

// amount is signed: positive = deposit, negative = withdrawal — keeps ConcurrentLedger's
// apply() to one code path instead of two near-duplicate methods.
public class TransactionEvent {

    private final String accountId;
    private final BigDecimal amount;

    public TransactionEvent(String accountId, BigDecimal amount) {
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
