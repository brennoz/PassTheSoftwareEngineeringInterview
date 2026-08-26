package com.interview.algorithms.java_features.q057_lazy_optional_callbacks.solution;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    private final Customer customer = new Customer("c1", "ada@example.com");

    @Test
    void greetReturnsWelcomeMessageWhenCustomerPresent() {
        String result = Solution.greet(Optional.of(customer), () -> "Welcome, stranger");

        assertEquals("Welcome back, ada@example.com", result);
    }

    // What this verifies: the actual point of the exercise. A version using
    // orElse(fallbackGreetingSupplier.get()) would call the supplier here too, since
    // orElse's argument is evaluated eagerly regardless of the Optional's state — this test
    // is what would catch that specific mistake, not the happy-path test above.
    @Test
    void greetDoesNotInvokeSupplierWhenCustomerPresent() {
        AtomicBoolean supplierWasCalled = new AtomicBoolean(false);

        Solution.greet(Optional.of(customer), () -> {
            supplierWasCalled.set(true);
            return "Welcome, stranger";
        });

        assertFalse(supplierWasCalled.get());
    }

    @Test
    void greetInvokesSupplierAndReturnsItsValueWhenCustomerAbsent() {
        String result = Solution.greet(Optional.empty(), () -> "Welcome, stranger");

        assertEquals("Welcome, stranger", result);
    }

    @Test
    void notifyCustomerCallsConsumerAndNotRunnableWhenPresent() {
        AtomicReference<Customer> notified = new AtomicReference<>();
        AtomicBoolean runnableWasCalled = new AtomicBoolean(false);

        Solution.notifyCustomer(Optional.of(customer), notified::set, () -> runnableWasCalled.set(true));

        assertEquals(customer, notified.get());
        assertFalse(runnableWasCalled.get());
    }

    @Test
    void notifyCustomerCallsRunnableAndNotConsumerWhenAbsent() {
        AtomicBoolean consumerWasCalled = new AtomicBoolean(false);
        AtomicBoolean runnableWasCalled = new AtomicBoolean(false);

        Solution.notifyCustomer(Optional.empty(), c -> consumerWasCalled.set(true), () -> runnableWasCalled.set(true));

        assertFalse(consumerWasCalled.get());
        assertTrue(runnableWasCalled.get());
    }

    @Test
    void functionalAndImperativeApproachesAgreeOnGreetOutput() {
        assertEquals(
                Solution.greet(Optional.of(customer), () -> "fallback"),
                ImperativeApproach.greet(Optional.of(customer), () -> "fallback")
        );
        assertEquals(
                Solution.greet(Optional.empty(), () -> "fallback"),
                ImperativeApproach.greet(Optional.empty(), () -> "fallback")
        );
    }
}
