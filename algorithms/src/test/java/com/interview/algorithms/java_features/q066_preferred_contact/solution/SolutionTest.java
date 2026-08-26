package com.interview.algorithms.java_features.q066_preferred_contact.solution;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    @Test
    void prefersMobileWhenPresentAndDoesNotConsultEmailLookup() {
        Optional<String> result = Solution.resolvePreferredContact(
                "+44 7000 000000",
                () -> Optional.of("should-not-be-used@example.com")
        );

        assertEquals(Optional.of("+44 7000 000000"), result);
    }

    @Test
    void fallsBackToEmailLookupWhenMobileAbsent() {
        Optional<String> result = Solution.resolvePreferredContact(null, () -> Optional.of("c2@example.com"));

        assertEquals(Optional.of("c2@example.com"), result);
    }

    @Test
    void returnsEmptyWhenNeitherPresent() {
        Optional<String> result = Solution.resolvePreferredContact(null, Optional::empty);

        assertTrue(result.isEmpty());
    }

    // What this verifies: the emailLookup supplier is genuinely never invoked when mobileNumber
    // is present — not just that its result is ignored. A version using
    // Optional.ofNullable(mobile).or(emailLookup.get()) (calling get() eagerly to build the
    // argument, instead of passing the Supplier itself to or()) would still return the right
    // value but would run the "expensive lookup" on every call regardless.
    @Test
    void neverInvokesEmailLookupWhenMobileIsPresent() {
        AtomicBoolean lookupRan = new AtomicBoolean(false);

        Solution.resolvePreferredContact("+44 7000 000000", () -> {
            lookupRan.set(true);
            return Optional.of("fallback@example.com");
        });

        assertFalse(lookupRan.get());
    }

    @Test
    void functionalAndImperativeApproachesAgreeOnResults() {
        assertEquals(
                ImperativeApproach.resolvePreferredContact(null, () -> Optional.of("c5@example.com")),
                Solution.resolvePreferredContact(null, () -> Optional.of("c5@example.com"))
        );
    }
}
