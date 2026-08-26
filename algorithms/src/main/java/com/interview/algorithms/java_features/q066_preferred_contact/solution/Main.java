package com.interview.algorithms.java_features.q066_preferred_contact.solution;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        System.out.println("Mobile present: " +
                Solution.resolvePreferredContact("+44 7000 000000", () -> {
                    System.out.println("  (email lookup ran — this line should NOT print here)");
                    return Optional.of("fallback@example.com");
                }));

        System.out.println("Mobile absent, email found: " +
                Solution.resolvePreferredContact(null, () -> Optional.of("c2@example.com")));

        System.out.println("Neither present: " +
                Solution.resolvePreferredContact(null, Optional::empty));
    }
}
