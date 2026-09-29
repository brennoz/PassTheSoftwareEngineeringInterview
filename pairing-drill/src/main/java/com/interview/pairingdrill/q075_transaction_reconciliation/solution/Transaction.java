package com.interview.pairingdrill.q075_transaction_reconciliation.solution;

import java.math.BigDecimal;

public class Transaction {

    private String id;
    private String currency;
    private BigDecimal amount;

    public Transaction() {
    }

    public Transaction(String id, String currency, BigDecimal amount) {
        this.id = id;
        this.currency = currency;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
