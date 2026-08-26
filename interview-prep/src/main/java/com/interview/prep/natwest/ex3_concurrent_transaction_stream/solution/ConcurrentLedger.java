package com.interview.prep.natwest.ex3_concurrent_transaction_stream.solution;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

// Same per-account ReentrantLock pattern as Ex1's Ledger — the point of Ex3 is proving this
// pattern still has to hold even when the concurrency comes from RxJava's own thread pool
// (Schedulers.io()) rather than an ExecutorService you created and can see. RxJava observers
// merged via Observable.merge with different subscribeOn() schedulers genuinely run on
// different threads — this is not a synthetic test setup, it's how a real multi-source
// event feed (e.g. several Kafka partitions consumed in parallel) behaves.
public class ConcurrentLedger {

    private final Map<String, BigDecimal> balances = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public void open(String accountId, BigDecimal openingBalance) {
        balances.put(accountId, openingBalance);
        locks.put(accountId, new ReentrantLock());
    }

    public BigDecimal balanceOf(String accountId) {
        return balances.get(accountId);
    }

    // Returns the resulting balance so the caller (StreamMain) has something to hand to the
    // persistence step without a second, separately-locked read racing against another writer.
    public BigDecimal apply(TransactionEvent event) {
        ReentrantLock lock = locks.get(event.getAccountId());
        try {
            if (!lock.tryLock(500, TimeUnit.MILLISECONDS)) {
                throw new IllegalStateException("Could not acquire lock for " + event.getAccountId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted acquiring lock for " + event.getAccountId());
        }
        try {
            BigDecimal updated = balances.get(event.getAccountId()).add(event.getAmount());
            balances.put(event.getAccountId(), updated);
            return updated;
        } finally {
            lock.unlock();
        }
    }
}
