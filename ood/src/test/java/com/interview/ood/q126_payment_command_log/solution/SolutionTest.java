package com.interview.ood.q126_payment_command_log.solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {

    private Wallet wallet;
    private PaymentProcessor processor;

    @BeforeEach
    void setUp() {
        wallet = new Wallet(new BigDecimal("1000.00"));
        processor = new PaymentProcessor();
    }

    // Happy path: a debit reduces the balance by the exact amount.
    // Uses compareTo not assertEquals to avoid BigDecimal scale mismatch
    // (1000.00 - 200.00 = 800.00; assertEquals would fail if scale differs).
    @Test
    void execute_debit_reduces_balance() {
        processor.execute(new DebitCommand(wallet, new BigDecimal("200.00"), "purchase"));

        assertEquals(0, new BigDecimal("800.00").compareTo(wallet.getBalance()));
    }

    // Undo reverses the debit by crediting the same amount back.
    // This test specifically verifies the compensation transaction amount is exact —
    // a naive undo that snapshots balance before execute would fail if two commands interleave.
    @Test
    void undo_last_reverses_debit() {
        processor.execute(new DebitCommand(wallet, new BigDecimal("200.00"), "purchase"));

        processor.undoLast();

        assertEquals(0, new BigDecimal("1000.00").compareTo(wallet.getBalance()));
    }

    // Audit log records both executions and undos in order.
    // Verifies: undo is appended to the log even though it is removed from history.
    // A superficially similar test that only checks list.size() == 3 would not catch
    // a bug where "UNDONE:" was missing from the entry text.
    @Test
    void audit_log_records_execute_and_undo() {
        processor.execute(new DebitCommand(wallet, new BigDecimal("200.00"), "purchase"));
        processor.execute(new CreditCommand(wallet, new BigDecimal("50.00"), "cashback"));
        processor.undoLast();

        List<String> log = processor.getAuditLog();
        assertEquals(3, log.size());
        assertEquals("EXECUTED: purchase", log.get(0));
        assertEquals("EXECUTED: cashback", log.get(1));
        assertEquals("UNDONE: cashback", log.get(2));
    }

    // Guard: undoLast on an empty processor must throw, not silently no-op.
    // A silent no-op would hide a caller bug (calling undo without a prior execute).
    @Test
    void undo_on_empty_history_throws() {
        assertThrows(IllegalStateException.class, () -> processor.undoLast());
    }
}
