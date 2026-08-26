package com.interview.ood.q036_bank_service_layer.solution;

// Pattern: Thin Data Container (Service Layer pattern).
// Contrast with Rich Domain Model (q026): Account there had deposit/withdraw inside.
// Here Account is a pure data holder — zero behaviour. All logic lives in AccountService.
// Benefit: AccountService is independently testable via Mockito without needing a real Account.
//
// Java 21 record: compiler generates equals(), hashCode(), toString(), and field accessors.
// Immutable by default — no setters. "Updating" status produces a new instance (see withStatus).
//
// In prod (JPA): annotated @Entity @Table(name="accounts"). id → @Id, type/status → @Enumerated(STRING).
// In prod (Redis cache): cached as JSON with key "account:{id}", TTL 5 min (cache-aside pattern).
//   On AccountService.close() the cache entry is evicted. Multi-pod risk: two pods can read
//   stale cache between eviction and next DB read. Fix: short TTL + Redis pub/sub eviction broadcast.
// In prod (Kafka): AccountOpened / AccountClosed events carry a snapshot of this record as payload.
public record Account(
        String        id,
        String        ownerId,
        AccountType   type,
        AccountStatus status
) {
    // Immutable update — returns a new record with changed status, original untouched.
    // Same pattern used in Event Sourcing: apply(event) → new state, never mutate.
    public Account withStatus(AccountStatus newStatus) {
        return new Account(id, ownerId, type, newStatus);
    }
}
