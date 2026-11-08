package com.interview.spring.q128_invoice_payment_service.solution;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

// Record DTO: immutable, equals/hashCode/toString generated, no boilerplate.
// Records are the correct type for request DTOs in Java 17+ — they signal "data carrier,
// not a domain object." A domain object has behaviour; a DTO is just structured input.
//
// @NotBlank: rejects null, empty, and whitespace-only strings. Stronger than @NotNull
// for String fields because " " (blank) is as useless as null for a customerId.
//
// @NotNull on dueDate: cannot be blank (it's a date, not a String). For future date
// enforcement add @Future — omitted here to keep the test setup simple (test creates
// past/present dates). In production, always validate dueDate > today in service.
//
// @Valid on the controller method parameter triggers these constraints.
// Without @Valid, the annotations are silently ignored — common interview gotcha.
public record CreateInvoiceRequest(
        @NotBlank String customerId,
        @NotNull  LocalDate dueDate
) {}
