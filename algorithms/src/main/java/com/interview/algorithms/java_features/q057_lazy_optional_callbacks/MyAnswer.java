package com.interview.algorithms.java_features.q057_lazy_optional_callbacks;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MyAnswer {

    // your implementation here

    public static String greet(Optional<Customer> maybeCustomer, Supplier<String> fallbackGreetingSupplier) {
        return maybeCustomer
                .map(c -> "Welcome back, " + c.email())
                .orElseGet(fallbackGreetingSupplier);

    }

    public static void notifyCustomer(Optional<Customer> maybeCustomer, Consumer<Customer> sendNotification, Runnable logNoCustomerFound) {
        maybeCustomer.ifPresentOrElse(sendNotification, logNoCustomerFound);
    }
}
