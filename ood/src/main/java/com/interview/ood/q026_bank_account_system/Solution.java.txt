package com.interview.ood.q026_bank_account_system;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Strategy pattern: swappable interest calculation without changing account code
interface InterestCalculationStrategy {
    BigDecimal calculate(BigDecimal balance, BigDecimal annualRate, int periods);
}

class SimpleInterestStrategy implements InterestCalculationStrategy {
    @Override
    public BigDecimal calculate(BigDecimal balance, BigDecimal annualRate, int periods) {
        // I = P * r * t
        return balance.multiply(annualRate).multiply(BigDecimal.valueOf(periods))
                      .setScale(2, RoundingMode.HALF_UP);
    }
}

class CompoundInterestStrategy implements InterestCalculationStrategy {
    @Override
    public BigDecimal calculate(BigDecimal balance, BigDecimal annualRate, int periods) {
        // I = P * (1 + r)^t - P
        BigDecimal factor = BigDecimal.ONE.add(annualRate)
                                         .pow(periods, MathContext.DECIMAL128);
        return balance.multiply(factor).subtract(balance)
                      .setScale(2, RoundingMode.HALF_UP);
    }
}

// Immutable value object — append-only record of account activity
record Transaction(Type type, BigDecimal amount, BigDecimal balanceAfter, Instant timestamp) {
    enum Type { DEPOSIT, WITHDRAWAL }
}

// Template Method: common deposit/withdraw flow; subclasses provide validation rules
abstract class BankAccount {

    protected BigDecimal balance;
    private boolean closed = false;
    private final List<Transaction> transactions = new ArrayList<>();

    protected BankAccount(BigDecimal initialBalance) {
        this.balance = initialBalance.setScale(2, RoundingMode.HALF_UP);
    }

    public final void deposit(BigDecimal amount) {
        checkOpen();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");
        balance = balance.add(amount).setScale(2, RoundingMode.HALF_UP);
        transactions.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance, Instant.now()));
    }

    public final void withdraw(BigDecimal amount) {
        checkOpen();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");
        validateWithdrawal(amount);
        balance = balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
        transactions.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, balance, Instant.now()));
    }

    // Hook: subclasses enforce their own withdrawal rules
    protected abstract void validateWithdrawal(BigDecimal amount);

    public BigDecimal getBalance() { return balance; }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactions);
    }

    public void close() { this.closed = true; }

    private void checkOpen() {
        if (closed) throw new IllegalStateException("Account is closed");
    }
}

class SavingsAccount extends BankAccount {

    private final BigDecimal minimumBalance;
    private final BigDecimal annualRate;
    private InterestCalculationStrategy strategy;

    public SavingsAccount(BigDecimal initialBalance, BigDecimal minimumBalance,
                          BigDecimal annualRate, InterestCalculationStrategy strategy) {
        super(initialBalance);
        this.minimumBalance = minimumBalance;
        this.annualRate = annualRate;
        this.strategy = strategy;
    }

    @Override
    protected void validateWithdrawal(BigDecimal amount) {
        BigDecimal balanceAfter = balance.subtract(amount);
        if (balanceAfter.compareTo(minimumBalance) < 0)
            throw new IllegalStateException(
                "Withdrawal would breach minimum balance of " + minimumBalance);
    }

    public void applyInterest(int periods) {
        BigDecimal interest = strategy.calculate(balance, annualRate, periods);
        deposit(interest);
    }

    public void setStrategy(InterestCalculationStrategy strategy) {
        this.strategy = strategy;
    }
}

class CheckingAccount extends BankAccount {

    private final BigDecimal overdraftLimit;

    public CheckingAccount(BigDecimal initialBalance, BigDecimal overdraftLimit) {
        super(initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    protected void validateWithdrawal(BigDecimal amount) {
        BigDecimal balanceAfter = balance.subtract(amount);
        if (balanceAfter.compareTo(overdraftLimit.negate()) < 0)
            throw new IllegalStateException(
                "Withdrawal exceeds overdraft limit of " + overdraftLimit);
    }
}
