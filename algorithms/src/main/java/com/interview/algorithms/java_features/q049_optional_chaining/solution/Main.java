package com.interview.algorithms.java_features.q049_optional_chaining.solution;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        User withEmail = new User("u1", Optional.of("alice@example.com"));
        User noEmail = new User("u2", Optional.empty());
        User malformedEmail = new User("u3", Optional.of("not-an-email"));

        System.out.println(Solution.resolveEmailDomain(Optional.of(withEmail)));       // example.com
        System.out.println(Solution.resolveEmailDomain(Optional.of(noEmail)));         // unknown
        System.out.println(Solution.resolveEmailDomain(Optional.of(malformedEmail)));  // unknown
        System.out.println(Solution.resolveEmailDomain(Optional.empty()));             // unknown
    }
}
