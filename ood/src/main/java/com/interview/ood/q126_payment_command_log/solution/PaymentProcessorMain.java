package com.interview.ood.q126_payment_command_log.solution;

import java.math.BigDecimal;

// Runnable demonstration: drives the payment processor through a realistic sequence.
// Shows: execute debit, execute credit, undo last, read audit log.
public class PaymentProcessorMain {

    public static void main(String[] args) {
        Wallet wallet = new Wallet(new BigDecimal("1000.00"));
        PaymentProcessor processor = new PaymentProcessor();

        System.out.println("Initial balance: " + wallet.getBalance());

        processor.execute(new DebitCommand(wallet, new BigDecimal("200.00"), "flight-booking-ref-A1B2"));
        System.out.println("After debit £200: " + wallet.getBalance()); // 800.00

        processor.execute(new CreditCommand(wallet, new BigDecimal("50.00"), "cashback-reward"));
        System.out.println("After credit £50: " + wallet.getBalance()); // 850.00

        processor.undoLast();
        System.out.println("After undo (reverses cashback): " + wallet.getBalance()); // 800.00

        System.out.println("\nAudit log:");
        processor.getAuditLog().forEach(entry -> System.out.println("  " + entry));

        // Expected output:
        //   EXECUTED: flight-booking-ref-A1B2
        //   EXECUTED: cashback-reward
        //   UNDONE:   cashback-reward
    }
}
