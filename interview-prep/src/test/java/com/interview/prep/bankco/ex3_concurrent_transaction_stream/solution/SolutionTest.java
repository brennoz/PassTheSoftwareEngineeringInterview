package com.interview.prep.bankco.ex3_concurrent_transaction_stream.solution;

import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    // The test that matters: events arrive from FOUR different threads (Schedulers.io(),
    // via subscribeOn in TransactionEventSource) and every single one must be applied to the
    // ledger without a lost update. Against an unlocked ledger this test is flaky-to-failing;
    // against ConcurrentLedger's per-account lock it's deterministic every run.
    @Test
    void concurrentEventsFromMultipleProducersAllApplyExactlyOnce() throws InterruptedException {
        ConcurrentLedger ledger = new ConcurrentLedger();
        ledger.open("acc-1", new BigDecimal("10000.00"));
        PersistenceGateway gateway = new PersistenceGateway();
        TransactionEventSource source = new TransactionEventSource();

        int producers = 4;
        int eventsPerProducer = 25;
        int totalEvents = producers * eventsPerProducer;
        BigDecimal withdrawalAmount = new BigDecimal("-10.00");

        Observable<TransactionEvent> events =
                source.concurrentEvents("acc-1", withdrawalAmount, producers, eventsPerProducer);

        CountDownLatch done = new CountDownLatch(totalEvents);
        events.subscribe(event -> {
            BigDecimal resultingBalance = ledger.apply(event);
            gateway.persist(event.getAccountId(), resultingBalance).thenRun(done::countDown);
        });

        assertTrue(done.await(10, TimeUnit.SECONDS));

        BigDecimal expected = new BigDecimal("10000.00")
                .add(withdrawalAmount.multiply(BigDecimal.valueOf(totalEvents)));
        assertEquals(0, ledger.balanceOf("acc-1").compareTo(expected));
        assertEquals(totalEvents, gateway.persistedCount());
    }
}
