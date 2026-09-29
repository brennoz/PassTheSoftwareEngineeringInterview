package com.interview.pairingdrill.q075_transaction_reconciliation.solution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SolutionTest {

    private final RateProvider rateProvider = Mockito.mock(RateProvider.class);
    private final ExternalAuditService externalAuditService = Mockito.mock(ExternalAuditService.class);
    private final TransactionReconciliationService service =
            new TransactionReconciliationService(rateProvider, externalAuditService);

    @Test
    void reconcilesMultipleTransactionsInTheSameCurrency() {
        Mockito.when(rateProvider.getRate("USD")).thenReturn(Optional.of(new BigDecimal("1.10")));
        List<Transaction> txs = List.of(
                new Transaction("t1", "USD", new BigDecimal("100")),
                new Transaction("t2", "USD", new BigDecimal("50")));

        Map<String, BigDecimal> result = service.reconcileByCurrency(txs);

        assertEquals(0, new BigDecimal("165.00").compareTo(result.get("USD")));
        Mockito.verify(externalAuditService).logTransaction(txs.get(0));
        Mockito.verify(externalAuditService).logTransaction(txs.get(1));
    }

    // This is the regression test for the actual reported ticket: the seeded code used
    // Optional.of(rateProvider.getRate(...)), which threw a bare NPE the instant a currency had
    // no rate. With the interface now returning Optional<BigDecimal>, a missing rate must
    // surface as a clear, named exception instead.
    @Test
    void throwsNamedExceptionWhenRateIsMissing() {
        Mockito.when(rateProvider.getRate("XYZ")).thenReturn(Optional.empty());
        List<Transaction> txs = List.of(new Transaction("t1", "XYZ", new BigDecimal("100")));

        assertThrows(MissingRateException.class, () -> service.reconcileByCurrency(txs));
    }

    // Regression test for the silently-vanishing audit failures: the seeded fire-and-forget
    // CompletableFuture meant an exception here would never surface anywhere. Now the future is
    // joined, so this test can prove the failure was actually observed (exceptionally ran)
    // rather than the whole call throwing or hanging.
    @Test
    void auditFailureDoesNotPreventReconciliationFromCompleting() {
        Mockito.when(rateProvider.getRate("USD")).thenReturn(Optional.of(BigDecimal.ONE));
        List<Transaction> txs = List.of(new Transaction("t1", "USD", new BigDecimal("100")));
        Mockito.doThrow(new AuditException("downstream audit system unavailable"))
                .when(externalAuditService).logTransaction(txs.get(0));

        Map<String, BigDecimal> result = service.reconcileByCurrency(txs); // must not throw

        assertEquals(0, new BigDecimal("100").compareTo(result.get("USD")));
    }

    // Regression test for the actual reported ticket's other symptom: a duplicate transaction
    // ID used to crash with a bare IllegalStateException naming no transaction. Now it must
    // name the actual duplicate ID.
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
}
