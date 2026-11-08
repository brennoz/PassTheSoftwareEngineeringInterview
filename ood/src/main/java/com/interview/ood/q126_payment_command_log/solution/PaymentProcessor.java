package com.interview.ood.q126_payment_command_log.solution;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

// Invoker in the Command pattern: executes commands without knowing what they do.
// Maintains two independent data structures:
//   history — a stack (LIFO) for undo; commands are popped in reverse order of execution.
//   auditLog — append-only list of every event (executions AND undos) for compliance.
//
// Why ArrayDeque not java.util.Stack?
//   Stack extends Vector, which synchronises every method (legacy Java 1.0 design).
//   ArrayDeque is the modern unsynchronised replacement — faster in single-threaded use.
//   addFirst/removeFirst give LIFO (stack) semantics.
//
// Why separate auditLog from history?
//   An undone command is removed from history (can't be re-undone) but stays in the
//   audit log — financial compliance requires an immutable record of every event,
//   including compensations. Merging the two would make the audit log mutable.
public class PaymentProcessor {

    private final Deque<PaymentCommand> history = new ArrayDeque<>();
    private final List<String> auditLog = new ArrayList<>();

    public void execute(PaymentCommand command) {
        command.execute();
        history.addFirst(command);
        auditLog.add("EXECUTED: " + command.description());
    }

    public void undoLast() {
        if (history.isEmpty()) {
            throw new IllegalStateException("Nothing to undo");
        }
        PaymentCommand last = history.removeFirst();
        last.undo();
        auditLog.add("UNDONE: " + last.description());
    }

    // Unmodifiable view: callers can read the log but cannot alter it.
    // In production this would be a durable, append-only event store (e.g. Postgres with
    // no UPDATE/DELETE, or an immutable Kafka topic — same principle as Event Sourcing).
    public List<String> getAuditLog() {
        return Collections.unmodifiableList(auditLog);
    }
}
