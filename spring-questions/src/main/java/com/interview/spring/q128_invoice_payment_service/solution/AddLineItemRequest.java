package com.interview.spring.q128_invoice_payment_service.solution;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

// @DecimalMin("0.01"): rejects zero-priced line items (a £0.00 charge is meaningless on
// a telecoms invoice — would indicate a data entry error or missing rate lookup).
// @Min(1) on int quantity: must order at least 1 unit.
// Using int not Integer for quantity: can never be null (int is a primitive) — saves
// a @NotNull annotation and avoids autoboxing overhead in loops.
public record AddLineItemRequest(
        @NotBlank String description,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
        @Min(1) int quantity
) {}
