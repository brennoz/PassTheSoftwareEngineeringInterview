package com.interview.prep.bankco.ex2_transaction_functional_toolkit.solution;

import io.reactivex.rxjava3.core.Observable;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ToolkitMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TransactionPipeline pipeline = new TransactionPipeline(new AccountFetcher(), new TransactionValidator());

        System.out.println("thenApply -> balance: " + pipeline.fetchBalance("acc-1").get());
        System.out.println("thenCompose -> validation approved: "
                + pipeline.validateTransaction(new Transaction("acc-1", new BigDecimal("100.00"))).get().isApproved());

        TransactionStreamProcessor processor = new TransactionStreamProcessor();
        Observable<BigDecimal> amounts = Observable.just(
                new BigDecimal("50.00"), new BigDecimal("-10.00"),
                new BigDecimal("9999999.00"), new BigDecimal("25.50"));
        System.out.println("RxJava valid total: " + processor.totalOfValidTransactions(amounts).blockingGet());

        ReferenceBuilder refs = new ReferenceBuilder();
        List<String> parts = List.of("NWB", "2026", "0042");
        System.out.println("reduce reference: " + refs.buildReferenceUsingReduce(parts).orElse("<empty>"));
        System.out.println("collectors reference: " + refs.buildReferenceUsingCollectors(parts));
        System.out.println("checksum: " + refs.checksum("601613"));
    }
}
