package com.interview.pairingdrill.q079_account_statement.solution;

import java.math.BigDecimal;
import java.util.List;

// Bonus fix (not a numbered planted issue but worth noting): holds accountId + masked account
// number instead of the full Account entity.
// Why: the seeded Statement(Account account, ...) forwarded customerId, full accountNumber,
// and balance to every downstream consumer (PDF renderer, REST response, email job). Any new
// field added to Account would silently appear in every statement without a deliberate decision.
// Alternative rejected: hold a StatementAccountDto inner record — adds indirection for two
// fields; inline strings are cleaner when masking is the only transformation needed.
// What NOT to do: expose raw Account — callers shouldn't need to know which fields are safe
// to render and which are internal (customerId is internal; accountId is safe to show).
public class Statement {

    private final String accountId;
    private final String maskedAccountNumber;  // e.g. "****5678"
    private final List<Transaction> transactions;
    private final BigDecimal totalCredits;     // was: double
    private final BigDecimal totalDebits;      // was: double

    public Statement(String accountId, String maskedAccountNumber,
                     List<Transaction> transactions,
                     BigDecimal totalCredits, BigDecimal totalDebits) {
        this.accountId = accountId;
        this.maskedAccountNumber = maskedAccountNumber;
        this.transactions = transactions;
        this.totalCredits = totalCredits;
        this.totalDebits = totalDebits;
    }

    public String getAccountId()                 { return accountId; }
    public String getMaskedAccountNumber()       { return maskedAccountNumber; }
    public List<Transaction> getTransactions()   { return transactions; }
    public BigDecimal getTotalCredits()          { return totalCredits; }
    public BigDecimal getTotalDebits()           { return totalDebits; }
}
