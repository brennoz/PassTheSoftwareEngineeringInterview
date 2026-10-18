package com.interview.pairingdrill.q079_account_statement;

public class Account {

    private final String accountId;
    private final String accountNumber; // full sort-code + account number, e.g. "20-00-00 12345678"
    private final String customerId;
    private final double balance;

    public Account(String accountId, String accountNumber, String customerId, double balance) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.balance = balance;
    }

    public String getAccountId()     { return accountId; }
    public String getAccountNumber() { return accountNumber; }
    public String getCustomerId()    { return customerId; }
    public double getBalance()       { return balance; }
}
