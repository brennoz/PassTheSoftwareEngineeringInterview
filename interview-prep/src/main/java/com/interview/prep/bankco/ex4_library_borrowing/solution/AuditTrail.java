package com.interview.prep.bankco.ex4_library_borrowing.solution;

import java.util.List;

// Why an interface, not just a method on Library: this is the seam that makes the Mockito
// @Spy demonstration possible AND legitimate — Library shouldn't know or care whether audit
// events end up in memory, a database, or a Kafka topic. Testing Library against a REAL
// InMemoryAuditTrail (via @Spy, not @Mock) means the test verifies Library actually produces
// a correct, retrievable audit record — not just that it "called some method once."
public interface AuditTrail {
    void record(String event);
    List<String> allEvents();
}
