package com.interview.ood.q129_order_management.solution;

import java.math.BigDecimal;
import java.time.Instant;

// Immutable value object representing a single fill received from the venue.
// An order may receive multiple Executions (partial fills) before reaching FILLED status.
// Why Instant for timestamp: venue execution timestamps are UTC epoch-based.
// What NOT to do: store as LocalDateTime — localised timestamps lose the UTC reference
// needed for cross-venue reconciliation and MiFID II transaction reporting.
public record Execution(
        String executionId,
        Price executionPrice,
        BigDecimal filledQuantity,
        Instant timestamp
) {
    public Execution {
        if (executionId == null || executionId.isBlank())
            throw new IllegalArgumentException("executionId required");
        if (executionPrice == null)
            throw new IllegalArgumentException("executionPrice required");
        if (filledQuantity == null || filledQuantity.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("filledQuantity must be positive: " + filledQuantity);
        if (timestamp == null)
            throw new IllegalArgumentException("timestamp required");
    }
}
