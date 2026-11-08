package com.interview.ood.q126_payment_command_log.solution;

import java.math.BigDecimal;

// Receiver in the Command pattern: owns the mutable balance.
// Only raw mutations here — no validation of amount or overdraft checking.
// Validation belongs in the command or invoker, not the receiver,
// so Wallet stays a pure state holder with no business rules.
// BigDecimal not double: floating-point arithmetic loses cents at scale.
public class Wallet {

    private BigDecimal balance;

    public Wallet(BigDecimal initialBalance) {
        this.balance = initialBalance;
    }

    public void debit(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
