package com.interview.algorithms.java_features.q066_preferred_contact.solution;

import java.util.Optional;
import java.util.function.Supplier;

// Trade-off vs Solution.java: laziness has to be earned by hand here — an explicit
// `if (mobileNumber == null)` guard before the lookup even runs. Get the guard order wrong
// (call emailLookup.get() first "just to have it ready") and the expensive fallback runs on
// every call regardless of whether it was ever needed. Optional.or() makes that ordering the
// only option available, not a discipline the caller has to maintain.
public class ImperativeApproach {

    public static Optional<String> resolvePreferredContact(String mobileNumber, Supplier<Optional<String>> emailLookup) {
        if (mobileNumber != null) {
            return Optional.of(mobileNumber);
        }
        return emailLookup.get();
    }
}
