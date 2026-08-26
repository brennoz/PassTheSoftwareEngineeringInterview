package com.interview.ood.q036_bank_service_layer.solution;

import java.math.BigDecimal;

// Pattern: Domain Exception — carries business meaning beyond a generic RuntimeException.
// Unchecked (extends RuntimeException) so callers aren't forced to catch unless they handle it.
//
// In prod (Spring REST): caught by @ControllerAdvice → HTTP 422 Unprocessable Entity.
//   Not 400 (request was structurally valid) — the domain rule rejected it.
// In prod (Kafka consumer): this exception signals a non-retryable failure.
//   The consumer sends the message to a Dead Letter Queue (DLQ) instead of retrying —
//   retrying an overdraft will always fail, burning throughput for no reason.
// In prod (distributed): with optimistic locking (@Version on the account entity),
//   a concurrent withdrawal could pass the balance check but then fail at DB flush.
//   The @Transactional rolls back and re-throws — same exception, same DLQ path.
public class InsufficientFundsException extends RuntimeException {

    private final String     accountId;
    private final BigDecimal requested;
    private final BigDecimal available;

    public InsufficientFundsException(String accountId, BigDecimal requested, BigDecimal available) {
        super(String.format("Account %s: requested %s but only %s available",
                accountId, requested, available));
        this.accountId = accountId;
        this.requested = requested;
        this.available = available;
    }

    public String     getAccountId() { return accountId; }
    public BigDecimal getRequested() { return requested; }
    public BigDecimal getAvailable() { return available; }
}
