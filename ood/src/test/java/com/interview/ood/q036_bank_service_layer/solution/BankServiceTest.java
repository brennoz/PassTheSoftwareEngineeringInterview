package com.interview.ood.q036_bank_service_layer.solution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Pattern: Facade test — BankService is a thin delegator.
// We only verify that BankService routes each call to the correct collaborator.
// We do NOT re-test AccountService or TransferService logic here — that's covered in their own tests.
// This keeps tests focused and avoids testing the same thing twice (DRY in tests).
@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock private AccountService  accountService;
    @Mock private TransferService transferService;
    @InjectMocks private BankService bankService;

    // ── openAccount delegates to accountService.open() ───────────────────────
    @Test
    void openAccount_delegates_to_account_service() {
        Account expected = new Account("id-1", "owner-1", AccountType.CURRENT, AccountStatus.ACTIVE);
        when(accountService.open("owner-1", AccountType.CURRENT)).thenReturn(expected);

        Account result = bankService.openAccount("owner-1", AccountType.CURRENT);

        assertEquals(expected, result);
        verify(accountService).open("owner-1", AccountType.CURRENT);
    }

    // ── deposit delegates to accountService.deposit() ────────────────────────
    @Test
    void deposit_delegates_to_account_service() {
        BigDecimal amount = new BigDecimal("200.00");
        Transaction tx = new Transaction("tx-1", "acc-1", TransactionType.CREDIT, amount, amount, Instant.now());
        when(accountService.deposit("acc-1", amount)).thenReturn(tx);

        Transaction result = bankService.deposit("acc-1", amount);

        assertEquals(tx, result);
        verify(accountService).deposit("acc-1", amount);
    }

    // ── transfer delegates to transferService.transfer() ─────────────────────
    @Test
    void transfer_delegates_to_transfer_service() {
        BigDecimal amount = new BigDecimal("50.00");
        Transfer expected = new Transfer("tr-1", "acc-from", "acc-to", amount, Instant.now());
        when(transferService.transfer("acc-from", "acc-to", amount)).thenReturn(expected);

        Transfer result = bankService.transfer("acc-from", "acc-to", amount);

        assertEquals(expected, result);
        verify(transferService).transfer("acc-from", "acc-to", amount);
    }

    // ── history delegates to accountService ───────────────────────────────────
    @Test
    void history_delegates_to_account_service() {
        List<Transaction> ledger = List.of(
            new Transaction("tx-1", "acc-1", TransactionType.CREDIT, new BigDecimal("100"), new BigDecimal("100"), Instant.now())
        );
        when(accountService.history("acc-1")).thenReturn(ledger);

        List<Transaction> result = bankService.history("acc-1");

        assertEquals(ledger, result);
        verify(accountService).history("acc-1");
    }
}
