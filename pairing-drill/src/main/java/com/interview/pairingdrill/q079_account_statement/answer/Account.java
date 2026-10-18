package com.interview.pairingdrill.q079_account_statement.answer;

import java.math.BigDecimal;

public class Account {

    private final String accountId;
    private final String accountNumber;
    private final String customerId;
    private final BigDecimal balance;

    public Account(String accountId, String accountNumber, String customerId, BigDecimal balance) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = balance;
    }

    public String getAccountId()     { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public String getCustomerId()    { return customerId; }
    public BigDecimal getBalance()       { return balance; }
}
