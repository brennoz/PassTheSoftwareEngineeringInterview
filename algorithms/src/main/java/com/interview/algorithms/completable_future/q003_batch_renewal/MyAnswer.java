package com.interview.algorithms.completable_future.q003_batch_renewal;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class MyAnswer {

    private final PlanService planService;
    private final PaymentMethodService paymentMethodService;
    private final ChargeService chargeService;
    private final long timeoutMs;   // per-charge timeout; tests pass 10L, Main.java passes 2000L

    public MyAnswer(PlanService planService,
                    PaymentMethodService paymentMethodService,
                    ChargeService chargeService,
                    long timeoutMs) {
        this.planService = planService;
        this.paymentMethodService = paymentMethodService;
        this.chargeService = chargeService;
        this.timeoutMs = timeoutMs;
    }

    public CompletableFuture<List<RenewalResult>> processRenewals(List<String> subscriptionIds) {

        List<CompletableFuture<RenewalResult>> futures = subscriptionIds.stream()
            .map(this::renewOne)   // each call returns CF<RenewalResult> immediately
            .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(ignored ->
                futures.stream()
                    .map(CompletableFuture::join)
                    .toList()
            );
    }

    private CompletableFuture<RenewalResult> renewOne(String subscriptionId) {

        record PlanAndPayment(SubscriptionPlan plan, PaymentMethod pm) {}

        return planService.getPlan(subscriptionId)
            .thenCombine(paymentMethodService.getPaymentMethod(subscriptionId),
                         PlanAndPayment::new)
            .thenCompose(pp ->
                chargeService.charge(pp.plan(), pp.pm())
                    .orTimeout(timeoutMs, MILLISECONDS))
            .thenApply(charge -> RenewalResult.success(subscriptionId, charge))
            .exceptionally(ex -> RenewalResult.failed(subscriptionId, extractMessage(ex)));
    }

    private static String extractMessage(Throwable ex) {
        Throwable cause = (ex.getCause() != null) ? ex.getCause() : ex;
        String msg = cause.getMessage();
        return (msg != null) ? msg : cause.getClass().getSimpleName();
    }
}
