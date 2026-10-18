package com.interview.pairingdrill.q079_account_statement.solution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SolutionTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private AccountStatementService service;

    private static final LocalDate JAN_1  = LocalDate.of(2024, 1, 1);
    private static final LocalDate JAN_31 = LocalDate.of(2024, 1, 31);
    private static final LocalDate FEB_15 = LocalDate.of(2024, 2, 15);

    private Account acct;
    private Transaction credit100;
    private Transaction debit40;
    private Transaction creditOutOfRange;

    @BeforeEach
    void setUp() {
        service = new AccountStatementService(accountRepository, transactionRepository);

        acct = new Account("a1", "20-00-00 12345678", "cust-1", 500.0);

        // Fix 3 + Fix 4: BigDecimal amounts, TransactionType enum
        credit100      = new Transaction("t1", "a1", new BigDecimal("100.00"),
                TransactionType.CREDIT, LocalDate.of(2024, 1, 10), "Salary");
        debit40        = new Transaction("t2", "a1", new BigDecimal("40.00"),
                TransactionType.DEBIT,  LocalDate.of(2024, 1, 15), "Rent");
        creditOutOfRange = new Transaction("t3", "a1", new BigDecimal("999.00"),
                TransactionType.CREDIT, FEB_15, "Bonus");
    }

    // Verifies Fix 3 (BigDecimal totals) and Fix 4 (enum filtering):
    // a correct sum is the point — comparing BigDecimal with compareTo avoids scale sensitivity.
    // A wrong test would use assertEquals(100.0, ...) (double) or assert the raw double sum.
    @Test
    void generateStatement_totals_credits_and_debits_correctly() {
        when(accountRepository.findById("a1")).thenReturn(Optional.of(acct));
        when(transactionRepository.findByAccountId("a1"))
                .thenReturn(List.of(credit100, debit40, creditOutOfRange));

        Statement stmt = service.generateStatement("a1", JAN_1, JAN_31);

        // credit100 (Jan 10) and debit40 (Jan 15) are in range; creditOutOfRange (Feb 15) is not
        assertEquals(0, stmt.getTotalCredits().compareTo(new BigDecimal("100.00")));
        assertEquals(0, stmt.getTotalDebits().compareTo(new BigDecimal("40.00")));
        assertEquals(2, stmt.getTransactions().size());
    }

    // Verifies Fix 2 (no swallowed exception): unknown account throws, not returns null.
    // A wrong test would assertNull(result) — that would pass against the seeded swallowed version.
    @Test
    void generateStatement_propagates_exception_for_unknown_account() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.generateStatement("unknown", JAN_1, JAN_31));
    }

    // Verifies Fix 5 (N+1 eliminated): generateBatch must call findByAccountIds once and
    // must NEVER call the per-account findByAccountId.
    // A wrong test would only assert the result list size, missing the repo-call pattern entirely.
    @Test
    void generateBatch_uses_bulk_fetch_not_per_account_calls() {
        Account acct2 = new Account("a2", "20-00-00 87654321", "cust-2", 200.0);
        Transaction credit50a2 = new Transaction("t4", "a2", new BigDecimal("50.00"),
                TransactionType.CREDIT, LocalDate.of(2024, 1, 20), "Transfer");

        when(accountRepository.findAllById(List.of("a1", "a2")))
                .thenReturn(List.of(acct, acct2));
        when(transactionRepository.findByAccountIds(List.of("a1", "a2")))
                .thenReturn(List.of(credit100, debit40, credit50a2));

        List<Statement> results = service.generateBatch(List.of("a1", "a2"), JAN_1, JAN_31);

        assertEquals(2, results.size());
        // Fix 5: the per-account method must never be called — not even once
        verify(transactionRepository, never()).findByAccountId(anyString());
    }

    // Verifies date-range filtering is applied correctly in generateStatement.
    // Uses two transactions spanning the boundary: one on the last day of range (inclusive),
    // one the day after (exclusive). Tests the isAfter/isBefore boundary condition directly.
    @Test
    void generateStatement_excludes_transactions_outside_date_range() {
        Transaction jan31Txn = new Transaction("t5", "a1", new BigDecimal("25.00"),
                TransactionType.DEBIT, JAN_31, "Subscription");
        Transaction feb1Txn = new Transaction("t6", "a1", new BigDecimal("25.00"),
                TransactionType.DEBIT, LocalDate.of(2024, 2, 1), "Subscription");

        when(accountRepository.findById("a1")).thenReturn(Optional.of(acct));
        when(transactionRepository.findByAccountId("a1"))
                .thenReturn(List.of(jan31Txn, feb1Txn));

        Statement stmt = service.generateStatement("a1", JAN_1, JAN_31);

        // jan31Txn is on the boundary (inclusive) — must be included
        // feb1Txn is after the end date — must be excluded
        assertEquals(1, stmt.getTransactions().size());
        assertEquals("t5", stmt.getTransactions().get(0).getTxId());
    }

    // Verifies generateBatch skips accounts not found and still returns statements for the rest.
    // This was handled correctly in the seeded code too; test confirms the fix didn't regress it.
    @Test
    void generateBatch_skips_accounts_not_returned_by_repository() {
        when(accountRepository.findAllById(anyList()))
                .thenReturn(List.of(acct)); // only "a1" found, "missing" not returned
        when(transactionRepository.findByAccountIds(anyList()))
                .thenReturn(List.of(credit100));

        List<Statement> results = service.generateBatch(List.of("a1", "missing"), JAN_1, JAN_31);

        assertEquals(1, results.size());
        assertEquals("a1", results.get(0).getAccountId());
    }
}
