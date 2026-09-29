package com.interview.prep.bankco.ex3_concurrent_transaction_stream.solution;

import io.reactivex.rxjava3.core.Observable;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StreamMain {

    public static void main(String[] args) throws InterruptedException {
        ConcurrentLedger ledger = new ConcurrentLedger();
        ledger.open("acc-1", new BigDecimal("10000.00"));

        PersistenceGateway gateway = new PersistenceGateway();
        TransactionEventSource source = new TransactionEventSource();

        int producers = 4;
        int eventsPerProducer = 25;
        int totalEvents = producers * eventsPerProducer;
        BigDecimal withdrawalAmount = new BigDecimal("-10.00");

        Observable<TransactionEvent> events = source.concurrentEvents(
                "acc-1", withdrawalAmount, producers, eventsPerProducer);

        CountDownLatch done = new CountDownLatch(totalEvents);

        // Each emission: apply to the lock-protected ledger (sync), THEN compose the async
        // persist step. thenCompose isn't needed here (persist doesn't depend on another
        // future) — thenRun is the right call since we only care that persistence completed,
        // not any value it returns.
        events.subscribe(event -> {
            BigDecimal resultingBalance = ledger.apply(event);
            gateway.persist(event.getAccountId(), resultingBalance).thenRun(done::countDown);
        });

        done.await(10, TimeUnit.SECONDS);

        System.out.println("Producers: " + producers + " x " + eventsPerProducer + " events each");
        System.out.println("Final balance: " + ledger.balanceOf("acc-1")
                + " (expected " + new BigDecimal("10000.00").add(withdrawalAmount.multiply(BigDecimal.valueOf(totalEvents))) + ")");
        System.out.println("Persisted records: " + gateway.persistedCount() + " (expected " + totalEvents + ")");
    }
}
