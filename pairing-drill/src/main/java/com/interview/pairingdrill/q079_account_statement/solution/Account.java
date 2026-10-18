package com.interview.pairingdrill.q079_account_statement.solution;

// Account entity: balance stays as double here — not a planted issue for this session.
// In production, balance would be BigDecimal for the same reasons as Transaction.amount.
public class Account {

    private final String accountId;
    private final String accountNumber; // full sort-code + account number
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
