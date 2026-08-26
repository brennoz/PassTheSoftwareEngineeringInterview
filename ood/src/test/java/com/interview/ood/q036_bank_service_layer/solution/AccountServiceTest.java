package com.interview.ood.q036_bank_service_layer.solution;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

// Pattern: Unit test with Mockito — AccountService is the sole unit under test.
// AccountRepository and LedgerRepository are mocked: we verify what the service
// ASKED the repo to do, not what the repo actually did. Each layer is tested in isolation.
//
// Why not integration test here?
// No Spring context, no DB — fast (~10ms per test). Real infra is verified manually
// via docker-compose + curl or a separate integration test suite (Testcontainers).
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    // @Mock: Mockito creates a proxy that records calls and returns defaults (null / 0 / empty).
    // @InjectMocks: Mockito calls the single constructor of AccountService,
    //   passing the two mocks above — equivalent to new AccountService(accountRepo, ledgerRepo).
    @Mock private AccountRepository accountRepo;
    @Mock private LedgerRepository  ledgerRepo;
    @InjectMocks private AccountService service;

    // ── RED 1 ────────────────────────────────────────────────────────────────────
    // open() must: generate an id, set status=ACTIVE, persist via accountRepo.save().
    // We don't care about the id value — just that it is non-null (UUID generated internally).
    @Test
    void open_saves_active_account_with_correct_owner_and_type() {
        // When
        Account account = service.open("owner-1", AccountType.CURRENT);

        // Then — assert the returned record
        assertNotNull(account.id(), "UUID must be generated");
        assertEquals("owner-1", account.ownerId());
        assertEquals(AccountType.CURRENT,  account.type());
        assertEquals(AccountStatus.ACTIVE, account.status());

        // Verify interaction — service MUST have called save() exactly once with this account.
        // In prod: save() would call accountRepo.save(entity) → Hibernate INSERT.
        verify(accountRepo, times(1)).save(account);
    }

    // ── RED 2 ────────────────────────────────────────────────────────────────────
    // deposit() must: look up the account, call addToBalance, record a CREDIT in the ledger.
    // We stub findById and addToBalance so the service gets the values it needs without a real DB.
    @Test
    void deposit_records_credit_transaction_and_updates_balance() {
        String accountId  = "acc-001";
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal after  = new BigDecimal("100.00");

        // Stub: when service asks "does this account exist?", return a known ACTIVE account.
        when(accountRepo.findById(accountId))
            .thenReturn(Optional.of(new Account(accountId, "owner-1", AccountType.CURRENT, AccountStatus.ACTIVE)));
        // Stub: when service credits the balance, tell it the new balance is 100.00.
        when(accountRepo.addToBalance(accountId, amount)).thenReturn(after);

        Transaction tx = service.deposit(accountId, amount);

        // The returned transaction must be a CREDIT with correct amounts.
        assertEquals(TransactionType.CREDIT, tx.type());
        assertEquals(amount, tx.amount());
        assertEquals(after,  tx.balanceAfter());
        assertNotNull(tx.createdAt(), "timestamp must be set");

        // The service must have told the ledger to record this transaction.
        // In prod: LedgerRepository is backed by an append-only Postgres table;
        // that same row is published to Kafka via the Outbox pattern.
        verify(ledgerRepo, times(1)).record(tx);
    }

    // ── RED 3 ────────────────────────────────────────────────────────────────────
    // withdraw() must: throw InsufficientFundsException when the repo rejects the debit.
    // The guard lives in subtractFromBalance (repo's responsibility per the spec),
    // so we stub it to throw — then verify the exception bubbles up from the service unchanged.
    @Test
    void withdraw_throws_when_repo_signals_insufficient_funds() {
        String accountId = "acc-002";

        when(accountRepo.findById(accountId))
            .thenReturn(Optional.of(new Account(accountId, "owner-1", AccountType.CURRENT, AccountStatus.ACTIVE)));
        when(accountRepo.subtractFromBalance(eq(accountId), any(BigDecimal.class)))
            .thenThrow(new InsufficientFundsException(accountId, new BigDecimal("500.00"), new BigDecimal("50.00")));

        assertThrows(
            InsufficientFundsException.class,
            () -> service.withdraw(accountId, new BigDecimal("500.00"))
        );

        // Ledger must NOT record a transaction for a failed withdrawal.
        // In prod: if we recorded then threw, we'd have a phantom debit in the audit log.
        verifyNoInteractions(ledgerRepo);
    }

    // ── RED 4 ────────────────────────────────────────────────────────────────────
    // close() must: reject if balance > 0; on zero balance, persist account as CLOSED.
    // Two scenarios → two assertions in one test (related state transitions, not separate concerns).
    @Test
    void close_rejects_non_zero_balance_and_persists_closed_status_when_zero() {
        String accountId = "acc-003";
        Account active   = new Account(accountId, "owner-1", AccountType.SAVINGS, AccountStatus.ACTIVE);

        // Scenario A — balance > 0 → must throw
        when(accountRepo.findById(accountId)).thenReturn(Optional.of(active));
        when(accountRepo.getBalance(accountId)).thenReturn(new BigDecimal("50.00"));

        assertThrows(IllegalStateException.class, () -> service.close(accountId));
        verify(accountRepo, never()).save(any()); // must NOT persist a CLOSED account on failure

        // Scenario B — balance == 0 → must save with status=CLOSED
        when(accountRepo.getBalance(accountId)).thenReturn(BigDecimal.ZERO);

        service.close(accountId);

        // Capture the Account passed to save() and assert it is CLOSED.
        // ArgumentCaptor: lets us inspect exact argument the service passed to the mock.
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepo, times(1)).save(captor.capture());
        assertEquals(AccountStatus.CLOSED, captor.getValue().status());
    }
}
