package com.interview.algorithms.completable_future.q003_batch_renewal;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class PlanServiceImpl {

    private ExecutorService executorService;
    private long delayMs;

    public PlanServiceImpl (ExecutorService executorService, long delayMs) {
        this.executorService = executorService;
        this.delayMs = delayMs;
    }

    public CompletableFuture<SubscriptionPlan> getPlan(String subscriptionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ie);
            }
            return new SubscriptionPlan(subscriptionId, "MONTHLY", BigDecimal.valueOf(50));
        }, executorService);
    }
}
