package com.interview.pairingdrill.q079_account_statement.answer;

import java.math.BigDecimal;
import java.util.List;

public class Statement {

    private final Account account;
    private final List<Transaction> transactions;
    private final BigDecimal totalCredits;
    private final BigDecimal totalDebits;

    public Statement(Account account, List<Transaction> transactions,
                     BigDecimal totalCredits, BigDecimal totalDebits) {
        this.account = account;
        this.transactions = transactions;
        this.totalCredits = totalCredits;
        this.totalDebits = totalDebits;
    }

    public Account getAccount()                  { return account; }
    public List<Transaction> getTransactions()   { return transactions; }
    public BigDecimal getTotalCredits()              { return totalCredits; }
    public BigDecimal getTotalDebits()               { return totalDebits; }
}
