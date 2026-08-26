package com.interview.algorithms.java_features.q057_lazy_optional_callbacks;

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void should_get_greeting_message_for_customer () {

        Customer maybeCustomer = new Customer("c1","john.doe@example.com");

        Supplier<String> fallbackGreetingSupplier = () -> "Ciao stranger!";

        String msg = MyAnswer.greet(Optional.of(maybeCustomer), fallbackGreetingSupplier);

        assertEquals("Welcome back, john.doe@example.com", msg);
    }

    @Test
    void should_deliver_a_joke_if_customer_is_not_present () {

        Supplier<String> fallbackGreetingSupplier = () -> "Ciao stranger!";

        String msg = MyAnswer.greet(Optional.empty(), fallbackGreetingSupplier);

        assertEquals("Ciao stranger!", msg);
    }    
}
