package com.interview.ood.q126_payment_command_log.solution;

// Command interface: every payment operation must support execute, undo, and a label for the audit log.
// Why an interface not an abstract class: no shared state between DebitCommand and CreditCommand —
// they only differ in which wallet operation they call. An abstract class would add nothing.
public interface PaymentCommand {
    void execute();
    void undo();
    String description();
}
