package com.interview.algorithms.java_features.q066_preferred_contact;

import java.util.Optional;
import java.util.function.Supplier;

public class MyAnswer {

    public static Optional<String> resolvePreferredContact(String mobileNumber, Supplier<Optional<String>> emailLookup) {
        return Optional.ofNullable(mobileNumber).or(emailLookup);
    }
}
