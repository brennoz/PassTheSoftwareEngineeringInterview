package com.interview.ood.q036_bank_service_layer.solution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// TransferService orchestrates two AccountService calls (withdraw + deposit).
// It doesn't touch repositories directly — AccountService owns balance logic.
// We mock AccountService and TransferRepository: test only TransferService decisions.
@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock private AccountService      accountService;
    @Mock private TransferRepository  transferRepo;
    @InjectMocks private TransferService service;

    // ── RED 1: same-account transfer is rejected immediately ──────────────────
    // No money should move — neither accountService nor transferRepo should be called.
    @Test
    void transfer_throws_when_from_and_to_are_the_same_account() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer("acc-1", "acc-1", new BigDecimal("50.00"))
        );

        verifyNoInteractions(accountService, transferRepo);
    }

    // ── RED 2: successful transfer wires withdraw → deposit → saves Transfer ──
    // Order matters: withdraw first (debit source), then deposit (credit destination).
    // If deposit fails after withdraw succeeds → money lost. In prod: @Transactional.
    @Test
    void transfer_withdraws_from_source_deposits_to_destination_and_saves_transfer() {
        String from   = "acc-from";
        String to     = "acc-to";
        BigDecimal amount = new BigDecimal("100.00");

        // Stub withdraw/deposit to return dummy Transactions (TransferService doesn't use them).
        Transaction debit  = new Transaction("tx-1", from, TransactionType.DEBIT,  amount, BigDecimal.ZERO, Instant.now());
        Transaction credit = new Transaction("tx-2", to,   TransactionType.CREDIT, amount, amount,           Instant.now());
        when(accountService.withdraw(from, amount)).thenReturn(debit);
        when(accountService.deposit(to,   amount)).thenReturn(credit);

        Transfer result = service.transfer(from, to, amount);

        // Verify call order: withdraw BEFORE deposit.
        // In prod: wrong order (deposit first) means destination credited even if source has no funds.
        var order = inOrder(accountService);
        order.verify(accountService).withdraw(from, amount);
        order.verify(accountService).deposit(to, amount);

        // Transfer record must be persisted.
        ArgumentCaptor<Transfer> captor = ArgumentCaptor.forClass(Transfer.class);
        verify(transferRepo).save(captor.capture());
        Transfer saved = captor.getValue();
        assertEquals(from,   saved.fromAccountId());
        assertEquals(to,     saved.toAccountId());
        assertEquals(amount, saved.amount());
        assertNotNull(saved.id());
        assertNotNull(saved.createdAt());

        // Returned transfer == what was saved.
        assertEquals(saved, result);
    }

    // ── RED 3: InsufficientFundsException from withdraw bubbles up unchanged ──
    // deposit must NOT be called if withdraw fails — no phantom credit.
    @Test
    void transfer_propagates_insufficient_funds_without_crediting_destination() {
        String from   = "acc-from";
        String to     = "acc-to";
        BigDecimal amount = new BigDecimal("999.00");

        when(accountService.withdraw(eq(from), eq(amount)))
            .thenThrow(new InsufficientFundsException(from, amount, new BigDecimal("10.00")));

        assertThrows(
            InsufficientFundsException.class,
            () -> service.transfer(from, to, amount)
        );

        verify(accountService, never()).deposit(any(), any());
        verifyNoInteractions(transferRepo);
    }
}
