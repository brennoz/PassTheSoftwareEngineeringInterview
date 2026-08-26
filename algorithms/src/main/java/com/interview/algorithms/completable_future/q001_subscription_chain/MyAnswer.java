package com.interview.algorithms.completable_future.q001_subscription_chain;

import java.util.concurrent.CompletableFuture;

public class MyAnswer {
    // your implementation here
    private final SubscriptionService subscriptionService;
    private final PricingService pricingService;
    private final AccountService accountService;

    public MyAnswer(SubscriptionService subscriptionService,
                    PricingService pricingService,
                    AccountService accountService) {
        this.subscriptionService = subscriptionService;
        this.pricingService = pricingService;
        this.accountService = accountService;
    }  
    
    public CompletableFuture<EnrichedSubscription> enrich(String subscriptionId) {
        return subscriptionService.findById(subscriptionId)
            .thenApply(subscription -> pricingService.applyDiscount(subscription))
            .thenCompose(discounted ->
                accountService.findById(discounted.accountId())
                    .thenApply(account -> new EnrichedSubscription(discounted, account))
            );
    }    
}
