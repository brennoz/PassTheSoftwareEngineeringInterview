package com.interview.pairingdrill.q075_transaction_reconciliation.answer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MyAnswerTest {

    private final RateProvider rateProvider = Mockito.mock(RateProvider.class);
    private final ExternalAuditService externalAuditService = Mockito.mock(ExternalAuditService.class);
    private final TransactionReconciliationService service =
            new TransactionReconciliationService(rateProvider, externalAuditService);

    @Test 
    void reconcilesMultipleTransactionsWithTheSameCurrency() {
        Mockito.when(rateProvider.getRate("GBP")).thenReturn(Optional.of(BigDecimal.valueOf(2)));
        List<Transaction> transactions = List.of(
                                                tx("tx-1", "GBP", BigDecimal.valueOf(100)),
                                                tx("tx-2", "GBP", BigDecimal.valueOf(500)),
                                                tx("tx-3", "GBP", BigDecimal.valueOf(400))
                                            );

        Map<String, BigDecimal> reconciledTxsByCurrency = service.reconcileByCurrency(transactions);

        assertEquals(1, reconciledTxsByCurrency.size());
        assertEquals(0, reconciledTxsByCurrency.get("GBP").compareTo(BigDecimal.valueOf(2000)));
        Mockito.verify(externalAuditService).logTransaction(transactions.get(0));
        Mockito.verify(externalAuditService).logTransaction(transactions.get(1));
        Mockito.verify(externalAuditService).logTransaction(transactions.get(2));               
    }

    @Test
    void throwsNamedExceptionWhenRateIsMissing() {
        Mockito.when(rateProvider.getRate("XYZ")).thenReturn(Optional.empty());
        List<Transaction> txs = List.of(tx("tx-1", "XYZ", new BigDecimal("100")));

        assertThrows(MissingRateException.class, () -> service.reconcileByCurrency(txs));
    }

    @Test 
    void auditFailureDoesNotPreventReconciliationFromCompleting() {
        Mockito.when(rateProvider.getRate("GBP")).thenReturn(Optional.of(BigDecimal.valueOf(2)));
        List<Transaction> transactions = List.of(
                                                tx("tx-1", "GBP", BigDecimal.valueOf(100)),
                                                tx("tx-2", "GBP", BigDecimal.valueOf(500)),
                                                tx("tx-3", "GBP", BigDecimal.valueOf(400))
                                            );
        Mockito.doThrow(new AuditException("downstream audit system unavailable"))
            .when(externalAuditService).logTransaction(transactions.get(1));

        Map<String, BigDecimal> reconciledTxsByCurrency = service.reconcileByCurrency(transactions);

        assertEquals(0, reconciledTxsByCurrency.get("GBP").compareTo(BigDecimal.valueOf(2000)));
    }

    @Test
    void namesTheDuplicateIdWhenTwoTransactionsShareOne() {
        List<Transaction> txs = List.of(
                new Transaction("dup-1", "USD", new BigDecimal("10")),
                new Transaction("dup-1", "USD", new BigDecimal("20")));

        DuplicateTransactionException ex =
                assertThrows(DuplicateTransactionException.class, () -> service.amountsById(txs));
        assertEquals(true, ex.getMessage().contains("dup-1"));
    }


    @Test
    void amountsByIdReturnsEachTransactionsAmountWhenNoDuplicates() {
        List<Transaction> txs = List.of(
                new Transaction("t1", "USD", new BigDecimal("10")),
                new Transaction("t2", "USD", new BigDecimal("20")));

        Map<String, BigDecimal> result = service.amountsById(txs);

        assertEquals(0, new BigDecimal("10").compareTo(result.get("t1")));
        assertEquals(0, new BigDecimal("20").compareTo(result.get("t2")));
    }    

    private Transaction tx(String id, String currency, BigDecimal amount) {
        return new Transaction(id, currency, amount);
    }
}
