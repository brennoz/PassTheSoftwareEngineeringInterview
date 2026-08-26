package com.interview.algorithms.completable_future.q002_invoice_builder;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class SubscriptionServiceImpl implements SubscriptionService{

    private final ExecutorService executorService;
    private final long delayMs;

    public SubscriptionServiceImpl(ExecutorService executorService, long delayMs) {
        this.executorService = executorService;
        this.delayMs = delayMs;
    }

    @Override
    public CompletableFuture<Subscription> getSubscription(String subscriptionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ie);
            }
            return new Subscription(subscriptionId, "ANNUAL", BigDecimal.valueOf(1200));
        }, executorService);
    }
}
