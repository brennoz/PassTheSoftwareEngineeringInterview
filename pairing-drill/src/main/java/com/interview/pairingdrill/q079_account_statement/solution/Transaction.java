package com.interview.pairingdrill.q079_account_statement.solution;

import java.math.BigDecimal;
import java.time.LocalDate;

// Fix 3: BigDecimal for amount — double loses sub-cent precision under floating-point rounding.
//   Example: 1_000_000 transactions each of £0.001 sums correctly in BigDecimal (£1000.000)
//   but double may accumulate error by the millionth iteration (£999.9999... or £1000.0001...).
//   In a banking context this means statements that don't reconcile with the ledger — a
//   regulatory issue, not just a cosmetic one.
//   Alternative rejected: double with rounding at display time — the rounding is applied after
//   the error has accumulated, so it masks rather than eliminates the problem.
// Fix 4: TransactionType enum for type — see TransactionType.java for rationale.
// What NOT to do: use float — same problem as double but with even less precision (23-bit
//   mantissa vs 52-bit). Never use any floating-point type for monetary values.
public class Transaction {

    private final String txId;
    private final String accountId;
    private final BigDecimal amount;      // was: double amount
    private final TransactionType type;   // was: String type
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

    public String getTxId()            { return txId; }
    public String getAccountId()       { return accountId; }
    public BigDecimal getAmount()      { return amount; }
    public TransactionType getType()   { return type; }
    public LocalDate getDate()         { return date; }
    public String getDescription()     { return description; }
}
