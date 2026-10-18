package com.interview.pairingdrill.q079_account_statement.answer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MyAnswerTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private AccountStatementService service;

    private static final LocalDate JAN_1  = LocalDate.of(2024, 1, 1);
    private static final LocalDate JAN_31 = LocalDate.of(2024, 1, 31);

    private Account acct;

    @BeforeEach
    void setUp() {
        service = new AccountStatementService(accountRepository, transactionRepository);
        acct = new Account("a1", "20-00-00 12345678", "cust-1", BigDecimal.valueOf(500.0));
    }

    @Test
    void should_get_bigdecimal_totals() {

        when(accountRepository.findById(acct.getAccountId())).thenReturn(Optional.of(acct));

        List<Transaction> txs = List.of(
                                    tx("tx-1", acct.getAccountId(), BigDecimal.valueOf(50.0), TransactionType.CREDIT, JAN_1, "My #1 transaction"),
                                    tx("tx-2", acct.getAccountId(), BigDecimal.valueOf(30.0), TransactionType.DEBIT, JAN_1, "My #2 transaction"),
                                    tx("tx-3", acct.getAccountId(), BigDecimal.valueOf(100.0), TransactionType.CREDIT, JAN_1, "My #3 transaction"),
                                    tx("tx-4", acct.getAccountId(), BigDecimal.valueOf(40.0), TransactionType.DEBIT, JAN_1, "My #4 transaction")
                                );

        when(transactionRepository.findByAccountId(acct.getAccountId())).thenReturn(txs);

        Statement statement = service.generateStatement(acct.getAccountId(), JAN_1, JAN_1);

        assertEquals(0, statement.getTotalCredits().compareTo(BigDecimal.valueOf(150)));
        assertEquals(0, statement.getTotalDebits().compareTo(BigDecimal.valueOf(70)));
    }

    @Test
    void should_exception_propagates_for_unknown_account() {
        when(accountRepository.findById("unknown")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.generateStatement("unknown", JAN_1, JAN_31));
    }

    @Test
    void should_batch_uses_bulk_fetch() {
        // Hint: stub findByAccountIds, call generateBatch, then verify that
        // findByAccountId (singular) was NEVER called.
        Account acct2 = new Account("a2", "20-00-01 12345678", "cust-2", BigDecimal.valueOf(1000.0));

        List<Account> accounts = List.of(acct, acct2);

        List<String> accountIds = accounts.stream().map(acc -> acc.getAccountId()).toList();

        when(accountRepository.findAllById(accountIds)).thenReturn(accounts);

        List<Transaction> txs = List.of(
                            tx("tx-1", acct.getAccountId(), BigDecimal.valueOf(50.0), TransactionType.CREDIT, JAN_1, "#1 transaction"),
                            tx("tx-2", acct.getAccountId(), BigDecimal.valueOf(30.0), TransactionType.DEBIT, JAN_1, "#2 transaction"),
                            tx("tx-3", acct.getAccountId(), BigDecimal.valueOf(100.0), TransactionType.CREDIT, JAN_1, "#3 transaction"),
                            tx("tx-4", acct.getAccountId(), BigDecimal.valueOf(40.0), TransactionType.DEBIT, JAN_1, "#4 transaction"),
                            tx("tx-5", acct2.getAccountId(), BigDecimal.valueOf(100.0), TransactionType.CREDIT, JAN_1, "#5 transaction"),
                            tx("tx-6", acct2.getAccountId(), BigDecimal.valueOf(30.0), TransactionType.DEBIT, JAN_1, "#6 transaction"),
                            tx("tx-7", acct2.getAccountId(), BigDecimal.valueOf(200.0), TransactionType.CREDIT, JAN_1, "#7 transaction"),
                            tx("tx-8", acct2.getAccountId(), BigDecimal.valueOf(70.0), TransactionType.DEBIT, JAN_1, "#8 transaction")
                        );

        when(transactionRepository.findByAccountIds(accountIds)).thenReturn(txs);

        List<Statement> statements = service.generateBatch(accountIds, JAN_1, JAN_1);

        Statement acc1Statement = statements.stream()
                                                .filter(s -> s.getAccount().getAccountId().equals(acct.getAccountId())).toList().getFirst();

        Statement acc2Statement = statements.stream()
                                                .filter(s -> s.getAccount().getAccountId().equals(acct2.getAccountId())).toList().getFirst();   

        assertEquals(0, acc1Statement.getTotalCredits().compareTo(BigDecimal.valueOf(150)));
        assertEquals(0, acc1Statement.getTotalDebits().compareTo(BigDecimal.valueOf(70)));
        assertEquals(0, acc2Statement.getTotalCredits().compareTo(BigDecimal.valueOf(300)));
        assertEquals(0, acc2Statement.getTotalDebits().compareTo(BigDecimal.valueOf(100)));

        verify(transactionRepository, never()).findByAccountId(anyString());
    }

    @Test
    void should_apply_date_range_filtering() {

        when(accountRepository.findById(acct.getAccountId())).thenReturn(Optional.of(acct));

        List<Transaction> txs = List.of(
                                    tx("tx-1", acct.getAccountId(), BigDecimal.valueOf(50.0), TransactionType.CREDIT, JAN_1, "My #1 transaction"),
                                    tx("tx-2", acct.getAccountId(), BigDecimal.valueOf(30.0), TransactionType.CREDIT, JAN_31, "My #2 transaction")
                                );

        when(transactionRepository.findByAccountId(acct.getAccountId())).thenReturn(txs);

        Statement statement = service.generateStatement(acct.getAccountId(), JAN_1, JAN_1);

        assertEquals(1, statement.getTransactions().size());
    }

    private Transaction tx(String txId, String accountId, BigDecimal amount, TransactionType type, LocalDate date, String description) {
        return new Transaction(txId, accountId, amount, type, date, description);
    }
}
