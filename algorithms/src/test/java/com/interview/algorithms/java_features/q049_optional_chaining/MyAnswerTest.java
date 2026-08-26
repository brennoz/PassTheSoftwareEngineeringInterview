package com.interview.algorithms.java_features.q049_optional_chaining;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MyAnswerTest {

    @Test
    void userWithValidEmailReturnsDomain() {
        User user = new User("u1", Optional.of("alice@example.com"));

        assertEquals("example.com", MyAnswer.resolveEmailDomain(Optional.of(user)));
    }

    @Test
    void emptyOptionalReturnsUnknown() {
        assertEquals("unknown", MyAnswer.resolveEmailDomain(Optional.empty()));
    }

    @Test
    void userWithNoEmailReturnsUnknown() {
        User user = new User("u2", Optional.empty());

        assertEquals("unknown", MyAnswer.resolveEmailDomain(Optional.of(user)));
    }

    @Test
    void userWithMalformedEmailReturnsUnknown() {
        User user = new User("u3", Optional.of("not-an-email"));

        assertEquals("unknown", MyAnswer.resolveEmailDomain(Optional.of(user)));
    }
}
