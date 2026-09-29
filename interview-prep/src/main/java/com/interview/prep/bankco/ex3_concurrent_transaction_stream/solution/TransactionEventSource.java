package com.interview.prep.bankco.ex3_concurrent_transaction_stream.solution;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Simulates several concurrent event producers (e.g. several Kafka partitions, or several
// upstream services each posting transactions) by merging N independent Observables, each
// subscribed on its own Schedulers.io() thread. Observable.merge interleaves emissions from
// all sources as they arrive — the resulting stream genuinely crosses threads, which is
// exactly the scenario that makes ConcurrentLedger's locking necessary rather than decorative.
public class TransactionEventSource {

    public Observable<TransactionEvent> concurrentEvents(String accountId, BigDecimal eventAmount,
                                                           int producers, int eventsPerProducer) {
        List<Observable<TransactionEvent>> sources = IntStream.range(0, producers)
                .mapToObj(producerId -> Observable
                        .range(0, eventsPerProducer)
                        .map(i -> new TransactionEvent(accountId, eventAmount))
                        .subscribeOn(Schedulers.io()))
                .collect(Collectors.toList());

        return Observable.merge(sources);
    }
}
