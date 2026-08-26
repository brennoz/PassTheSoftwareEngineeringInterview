package com.interview.algorithms.java_features.q049_optional_chaining;

import java.util.Optional;

public class MyAnswer {
    // your implementation here

    public static String resolveEmailDomain(Optional<User> maybeUser) {
        return maybeUser.flatMap(User::email)
                .filter(email -> email.contains("@"))
                .map(result -> result.substring(result.indexOf("@") + 1))
                .orElse("unknown");
    }
}
