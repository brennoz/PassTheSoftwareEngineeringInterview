package com.interview.algorithms.completable_future.q001_subscription_chain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MyAnswerTest {

    @Mock SubscriptionService subscriptionService;
    @Mock PricingService pricingService;
    @Mock AccountService accountService;

    MyAnswer underTest;

    @BeforeEach
    void setUp() {
        // q001 has no timeout — constructor takes 3 services only
        underTest = new MyAnswer(subscriptionService, pricingService, accountService);
    }    

    @Test
    void enrichReturnsCombinedSubscriptionAndAccount() throws Exception {
        var sub        = new Subscription("sub-1", "acc-1", "ANNUAL", BigDecimal.valueOf(1200));
        var discounted = new DiscountedSubscription("sub-1", "acc-1", "ANNUAL", BigDecimal.valueOf(1080));
        var account    = new Account("acc-1", "Acme Corp", "billing@acme.com");

        // CF: completedFuture — already-resolved CF. Callbacks fire synchronously on the calling thread.
        //     No thread pool involved in tests — deterministic and fast.
        when(subscriptionService.findById("sub-1")).thenReturn(CompletableFuture.completedFuture(sub));
        when(pricingService.applyDiscount(sub)).thenReturn(discounted);
        when(accountService.findById("acc-1")).thenReturn(CompletableFuture.completedFuture(account));

        EnrichedSubscription result = underTest.enrich("sub-1").get();

        assertEquals(result.subscription(), discounted);
        assertEquals(result.account(), account);
    }

    @Test
    void accountFetchedUsingAccountIdFromDiscountedSubscription() throws Exception {
        var sub        = new Subscription("sub-1", "acc-99", "MONTHLY", BigDecimal.valueOf(100));
        var discounted = new DiscountedSubscription("sub-1", "acc-99", "MONTHLY", BigDecimal.valueOf(90));
        var account    = new Account("acc-99", "Beta Ltd", "pay@beta.com");

        when(subscriptionService.findById("sub-1")).thenReturn(CompletableFuture.completedFuture(sub));
        when(pricingService.applyDiscount(sub)).thenReturn(discounted);
        when(accountService.findById("acc-99")).thenReturn(CompletableFuture.completedFuture(account));

        underTest.enrich("sub-1").get();

        // Mockito: verify() asserts the mock was called with specific args.
        // Catches bugs where implementation hardcodes an accountId or reads from the wrong object.
        verify(accountService).findById("acc-99");
    }
    
    @Test
    void discountAmountReflectedInResult() throws Exception {
        var sub        = new Subscription("sub-1", "acc-1", "ANNUAL", BigDecimal.valueOf(1200));
        var discounted = new DiscountedSubscription("sub-1", "acc-1", "ANNUAL", BigDecimal.valueOf(1080));
        var account    = new Account("acc-1", "Acme Corp", "billing@acme.com");

        when(subscriptionService.findById("sub-1")).thenReturn(CompletableFuture.completedFuture(sub));
        when(pricingService.applyDiscount(sub)).thenReturn(discounted);
        when(accountService.findById("acc-1")).thenReturn(CompletableFuture.completedFuture(account));

        EnrichedSubscription result = underTest.enrich("sub-1").get();

        // AssertJ: isEqualByComparingTo for BigDecimal — avoids scale differences.
        assertEquals(result.subscription().discountedAmount(), BigDecimal.valueOf(1080));
    }   

    @Test
    void subscriptionNotFoundCompletesExceptionally() {
        // CF: failedFuture — already-failed CF. Chained thenApply/thenCompose are skipped.
        //     Exception propagates until .exceptionally() or .handle() — Solution has neither,
        //     so the failure propagates to the caller.
        when(subscriptionService.findById("sub-999"))
            .thenReturn(CompletableFuture.failedFuture(
                new RuntimeException("Subscription not found: sub-999")));

        // .get() re-throws wrapped in ExecutionException — CF's cross-thread exception carrier.
        ExecutionException ex = assertThrows(ExecutionException.class,
            () -> underTest.enrich("sub-999").get());
        assertInstanceOf(RuntimeException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Subscription not found"));
    }    
}
