package com.interview.ood.q126_payment_command_log.solution;

import java.math.BigDecimal;

// Concrete Command: encapsulates a credit operation on a Wallet.
// Undo of a credit = debit the same amount (compensation transaction).
// Mirror image of DebitCommand — the only difference is which wallet method
// execute() and undo() call. Duplicating the class rather than sharing an
// abstract base is intentional: each class is 10 lines, readable in isolation.
// An abstract PaymentCommandBase with a direction enum would not reduce complexity here.
public class CreditCommand implements PaymentCommand {

    private final Wallet wallet;
    private final BigDecimal amount;
    private final String description;

    public CreditCommand(Wallet wallet, BigDecimal amount, String description) {
        this.wallet = wallet;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public void execute() {
        wallet.credit(amount);
    }

    @Override
    public void undo() {
        wallet.debit(amount);
    }

    @Override
    public String description() {
        return description;
    }
}
