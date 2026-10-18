package com.interview.pairingdrill.q079_account_statement;

import java.time.LocalDate;

public class Transaction {

    private final String txId;
    private final String accountId;
    private final double amount;   // amount in GBP
    private final String type;     // "CREDIT" or "DEBIT"
    private final LocalDate date;
    private final String description;

    public Transaction(String txId, String accountId, double amount,
                       String type, LocalDate date, String description) {
        this.txId = txId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.description = description;
    }

    public String getTxId()          { return txId; }
    public String getAccountId()     { return accountId; }
    public double getAmount()        { return amount; }
    public String getType()          { return type; }
    public LocalDate getDate()       { return date; }
    public String getDescription()   { return description; }
}
