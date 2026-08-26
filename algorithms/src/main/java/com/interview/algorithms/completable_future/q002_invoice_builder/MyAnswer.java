package com.interview.algorithms.completable_future.q002_invoice_builder;

import java.util.concurrent.CompletableFuture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class MyAnswer {

    private final SubscriptionService subscriptionService;
    private final BillingAddressService billingAddressService;
    private final PaymentMethodService paymentMethodService;
    private final long timeoutMs;

    public MyAnswer(SubscriptionService subscriptionService,
                    BillingAddressService billingAddressService,
                    PaymentMethodService paymentMethodService,
                    long timeoutMs) {
        this.subscriptionService = subscriptionService;
        this.billingAddressService = billingAddressService;
        this.paymentMethodService = paymentMethodService;
        this.timeoutMs = timeoutMs;
    }

    public CompletableFuture<Invoice> buildInvoice(String subscriptionId, String accountId) {

        CompletableFuture<Subscription> subCf = subscriptionService.getSubscription(subscriptionId)
            .completeOnTimeout(Subscription.empty(), timeoutMs, MILLISECONDS)
            .exceptionally(ex -> Subscription.empty());

        CompletableFuture<BillingAddress> addrCf = billingAddressService.getBillingAddress(accountId)
            .completeOnTimeout(BillingAddress.unknown(), timeoutMs, MILLISECONDS)
            .exceptionally(ex -> BillingAddress.unknown());

        CompletableFuture<PaymentMethod> pmCf = paymentMethodService.getDefaultPaymentMethod(accountId)
            .completeOnTimeout(PaymentMethod.unknown(), timeoutMs, MILLISECONDS)
            .exceptionally(ex -> PaymentMethod.unknown());

        return CompletableFuture.allOf(subCf, addrCf, pmCf)
            .thenApply(ignored -> {
                return new Invoice(subCf.join(), addrCf.join(), pmCf.join());
            });
    }
}