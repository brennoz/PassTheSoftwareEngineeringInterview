package com.interview.algorithms.java_features.q066_preferred_contact.solution;

import java.util.Optional;
import java.util.function.Supplier;

public class Solution {

    // Optional.or(Supplier<? extends Optional<? extends T>>) — distinct from orElse/orElseGet:
    // those two unwrap to a plain T (a real value or a computed fallback value). or() stays in
    // Optional-space — "this Optional if present, otherwise the Optional the supplier produces"
    // — which composes cleanly when the fallback source is itself something that can be absent
    // (here: an email lookup that might not find anything either).
    //
    // Laziness matters here specifically because emailLookup models an expensive fallback (a
    // DB/service call in a real system, not a free field read) — mirrors orElseGet's laziness
    // (q057), one level up at the Optional-returning layer instead of the value-returning
    // layer. If mobileNumber is present, emailLookup.get() must never run at all.
    public static Optional<String> resolvePreferredContact(String mobileNumber, Supplier<Optional<String>> emailLookup) {
        return Optional.ofNullable(mobileNumber).or(emailLookup);
    }
}
