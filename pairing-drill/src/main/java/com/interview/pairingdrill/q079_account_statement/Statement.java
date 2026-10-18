package com.interview.pairingdrill.q079_account_statement;

import java.util.List;

public class Statement {

    private final Account account;
    private final List<Transaction> transactions;
    private final double totalCredits;
    private final double totalDebits;

    public Statement(Account account, List<Transaction> transactions,
                     double totalCredits, double totalDebits) {
        this.account = account;
        this.transactions = transactions;
        this.totalCredits = totalCredits;
        this.totalDebits = totalDebits;
    }

    public Account getAccount()                  { return account; }
    public List<Transaction> getTransactions()   { return transactions; }
    public double getTotalCredits()              { return totalCredits; }
    public double getTotalDebits()               { return totalDebits; }
}
