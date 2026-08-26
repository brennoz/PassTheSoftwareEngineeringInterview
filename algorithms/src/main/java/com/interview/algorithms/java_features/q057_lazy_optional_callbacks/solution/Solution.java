package com.interview.algorithms.java_features.q057_lazy_optional_callbacks.solution;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Solution {

    // orElseGet(Supplier), not orElse(T): Java evaluates method arguments eagerly, before the
    // method body runs. optional.orElse(fallbackGreetingSupplier.get()) would call .get() on
    // EVERY invocation, present or not, because the argument has to be evaluated to a value
    // before orElse() can even be called. orElseGet() instead receives the Supplier itself
    // (unevaluated) and only calls .get() internally, and only on the path where the value is
    // actually needed — the Optional being present short-circuits it entirely.
    //
    // Rejected alternative: optional.map(c -> "Welcome back, " + c.email()).orElse(fallback...get()).
    // Rejected for the same reason — orElse's argument is still evaluated eagerly regardless
    // of whether map() produced a value, so wrapping the present-case logic in map() first
    // doesn't fix the underlying laziness bug at all.
    public static String greet(Optional<Customer> maybeCustomer, Supplier<String> fallbackGreetingSupplier) {
        return maybeCustomer
                .map(customer -> "Welcome back, " + customer.email())
                .orElseGet(fallbackGreetingSupplier);
    }

    // ifPresentOrElse(Consumer, Runnable) — a Runnable, not a second Consumer<Customer>, for
    // the absent branch: there IS no Customer to hand to a callback when the Optional is
    // empty, so a Consumer<Customer> would have nothing valid to receive. Runnable's whole
    // contract is "do something, take nothing, return nothing" — exactly the shape the
    // absent-case action needs.
    public static void notifyCustomer(Optional<Customer> maybeCustomer, Consumer<Customer> sendNotification, Runnable logNoCustomerFound) {
        maybeCustomer.ifPresentOrElse(sendNotification, logNoCustomerFound);
    }
}
