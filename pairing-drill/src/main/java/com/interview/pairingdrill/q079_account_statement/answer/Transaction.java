package com.interview.pairingdrill.q079_account_statement.answer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private final String txId;
    private final String accountId;
    private final BigDecimal amount;
    private final TransactionType type;
    private final LocalDate date;
    private final String description;

    public Transaction(String txId, String accountId, BigDecimal amount,
                       TransactionType type, LocalDate date, String description) {
        this.txId = txId;
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.description = description;
    }

    public String getTxId()          { return txId; }
    public String getAccountId()     { return accountId; }
    public BigDecimal getAmount()        { return amount; }
    public TransactionType getType()          { return type; }
    public LocalDate getDate()       { return date; }
    public String getDescription()   { return description; }
}
