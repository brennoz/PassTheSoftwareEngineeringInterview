package com.interview.algorithms.java_features.q057_lazy_optional_callbacks.solution;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        Customer customer = new Customer("c1", "ada@example.com");

        System.out.println("=== greet(), customer present — fallback supplier must NOT run ===");
        String greeting = Solution.greet(Optional.of(customer), () -> {
            System.out.println("  (fallback supplier invoked — this line should NOT print)");
            return "Welcome, stranger";
        });
        System.out.println(greeting);

        System.out.println();
        System.out.println("=== greet(), customer absent — fallback supplier SHOULD run ===");
        String fallbackGreeting = Solution.greet(Optional.empty(), () -> {
            System.out.println("  (fallback supplier invoked — this line SHOULD print)");
            return "Welcome, stranger";
        });
        System.out.println(fallbackGreeting);

        System.out.println();
        System.out.println("=== notifyCustomer(), both branches ===");
        Solution.notifyCustomer(Optional.of(customer),
                c -> System.out.println("Notified: " + c.email()),
                () -> System.out.println("No customer to notify"));
        Solution.notifyCustomer(Optional.empty(),
                c -> System.out.println("Notified: " + c.email()),
                () -> System.out.println("No customer to notify"));
    }
}
