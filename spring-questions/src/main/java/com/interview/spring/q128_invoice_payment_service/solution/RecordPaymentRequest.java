package com.interview.spring.q128_invoice_payment_service.solution;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

// Minimal DTO — payment amount only. In a real system, this would also include:
// paymentMethod (CARD, BANK_TRANSFER, DIRECT_DEBIT), paymentReference (idempotency key),
// and transactionDate. Omitted here to keep the question focused on the core patterns.
//
// Why no @DecimalMax? Overpayment is a domain concern (amount > amountDue), not a
// validation concern — the Invoice domain object rejects it with IllegalArgumentException.
// Validation annotations handle structural rules (not null, positive amount);
// domain methods handle business rules (can't overpay).
public record RecordPaymentRequest(
        @NotNull @DecimalMin("0.01") BigDecimal amount
) {}
