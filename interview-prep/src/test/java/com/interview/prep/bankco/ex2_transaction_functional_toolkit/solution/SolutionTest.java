package com.interview.prep.bankco.ex2_transaction_functional_toolkit.solution;

import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    private final TransactionPipeline pipeline =
            new TransactionPipeline(new AccountFetcher(), new TransactionValidator());
    private final TransactionStreamProcessor processor = new TransactionStreamProcessor();
    private final ReferenceBuilder refs = new ReferenceBuilder();

    @Test
    void thenApplyTransformsAlreadyAvailableValue() throws ExecutionException, InterruptedException {
        BigDecimal balance = pipeline.fetchBalance("acc-1").get();

        assertEquals(0, balance.compareTo(new BigDecimal("500.00")));
    }

    @Test
    void thenComposeFlattensTwoChainedAsyncCalls() throws ExecutionException, InterruptedException {
        Transaction transaction = new Transaction("acc-1", new BigDecimal("100.00"));

        ValidationResult result = pipeline.validateTransaction(transaction).get();

        assertTrue(result.isApproved());
    }

    @Test
    void rxJavaFiltersAndReducesToValidTotal() {
        Observable<BigDecimal> amounts = Observable.just(
                new BigDecimal("50.00"),
                new BigDecimal("-10.00"),          // filtered out: not positive
                new BigDecimal("9999999.00"),      // filtered out: over the single-tx limit
                new BigDecimal("25.50"));

        BigDecimal total = processor.totalOfValidTransactions(amounts).blockingGet();

        assertEquals(0, total.compareTo(new BigDecimal("75.50")));
    }

    @Test
    void reduceAndCollectorsProduceTheSameJoinButHandleEmptyDifferently() {
        List<String> parts = List.of("NWB", "2026", "0042");

        assertEquals(Optional.of("NWB-2026-0042"), refs.buildReferenceUsingReduce(parts));
        assertEquals("NWB-2026-0042", refs.buildReferenceUsingCollectors(parts));

        // The actual behavioral difference: empty input.
        assertEquals(Optional.empty(), refs.buildReferenceUsingReduce(List.of()));
        assertEquals("", refs.buildReferenceUsingCollectors(List.of()));
    }

    @Test
    void checksumReducesOverCharCodes() {
        int checksum = refs.checksum("601613");

        // '6'+'0'+'1'+'6'+'1'+'3' char codes summed — verifies IntStream.reduce, not a guess.
        int expected = "601613".chars().sum();
        assertEquals(expected, checksum);
    }
}
