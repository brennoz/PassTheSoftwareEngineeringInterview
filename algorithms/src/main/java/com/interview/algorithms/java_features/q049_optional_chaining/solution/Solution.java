package com.interview.algorithms.java_features.q049_optional_chaining.solution;

import java.util.Optional;

public class Solution {

    // flatMap, not map, on User::email — email() already returns Optional<String>, so
    // map(User::email) would produce Optional<Optional<String>>. flatMap flattens that one
    // level. Same reasoning applies anywhere a chained step itself returns an Optional
    // (or a Stream, for flatMap on streams) — map wraps, flatMap unwraps-and-chains.
    //
    // filter() before the substring map(): validity of the email ("has an @") is a
    // short-circuiting condition, not a transformation — filter is the Optional method for
    // "keep going only if true", exactly like Stream.filter.
    public static String resolveEmailDomain(Optional<User> maybeUser) {
        return maybeUser
                .flatMap(User::email)
                .filter(email -> email.contains("@"))
                .map(email -> email.substring(email.indexOf('@') + 1))
                .orElse("unknown");
    }
}
