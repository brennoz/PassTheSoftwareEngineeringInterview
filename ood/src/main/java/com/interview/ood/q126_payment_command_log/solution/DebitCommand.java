package com.interview.ood.q126_payment_command_log.solution;

import java.math.BigDecimal;

// Concrete Command: encapsulates a debit operation on a Wallet.
// Holds both the receiver (wallet) and the exact amount — undo() needs the amount
// to credit back precisely. Without storing it here the Invoker would have to track
// per-command state, breaking separation of concerns.
// Undo of a debit = credit the same amount (compensation transaction).
public class DebitCommand implements PaymentCommand {

    private final Wallet wallet;
    private final BigDecimal amount;
    private final String description;

    public DebitCommand(Wallet wallet, BigDecimal amount, String description) {
        this.wallet = wallet;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public void execute() {
        wallet.debit(amount);
    }

    // Compensation: reverse a debit by crediting the same amount.
    // Not wallet.debit(-amount): subtracting a negative is technically correct
    // but semantically confusing and error-prone under code review.
    @Override
    public void undo() {
        wallet.credit(amount);
    }

    @Override
    public String description() {
        return description;
    }
}
