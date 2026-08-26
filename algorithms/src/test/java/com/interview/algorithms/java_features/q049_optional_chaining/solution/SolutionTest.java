package com.interview.algorithms.java_features.q049_optional_chaining.solution;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class SolutionTest {

    @Test
    void userWithValidEmailReturnsDomain() {
        User user = new User("u1", Optional.of("alice@example.com"));

        assertEquals("example.com", Solution.resolveEmailDomain(Optional.of(user)));
    }

    @Test
    void emptyOptionalReturnsUnknown() {
        assertEquals("unknown", Solution.resolveEmailDomain(Optional.empty()));
    }

    @Test
    void userWithNoEmailReturnsUnknown() {
        User user = new User("u2", Optional.empty());

        assertEquals("unknown", Solution.resolveEmailDomain(Optional.of(user)));
    }

    // What this verifies: the filter() step actually gates on "@" presence rather than
    // substring() being allowed to throw/misbehave on malformed input — a version that
    // skipped filter() would either throw (indexOf returns -1, substring(0) silently wrong)
    // or silently return the whole string as the "domain", not "unknown".
    @Test
    void userWithMalformedEmailReturnsUnknown() {
        User user = new User("u3", Optional.of("not-an-email"));

        assertEquals("unknown", Solution.resolveEmailDomain(Optional.of(user)));
    }
}
