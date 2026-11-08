package com.interview.spring.q128_invoice_payment_service.solution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Response DTO — decouples the API contract from the JPA entity.
// Why not return the Invoice entity directly from the controller?
// (1) Circular serialisation: @OneToMany + lazy loading can cause LazyInitializationException
//     outside a transaction, or worse, infinite recursion in Jackson.
// (2) Coupling: any field added to Invoice for persistence (e.g. @Version) would appear
//     in the API response without intent — a schema change is an API change.
// (3) Control: computed fields (totalAmount, amountDue) are calculated once here,
//     not recalculated on every serialisation attempt.
//
// Static factory from() keeps the mapping logic in the DTO, not the service.
// Service stays focused on orchestration; mapping is a presentation concern.
public record InvoiceResponse(
        String id,
        String customerId,
        String status,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal amountDue,
        LocalDate issueDate,
        LocalDate dueDate,
        List<LineItemResponse> lineItems
) {
    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
            invoice.getId(),
            invoice.getCustomerId(),
            invoice.getStatus().name(),
            invoice.totalAmount(),
            invoice.getPaidAmount(),
            invoice.amountDue(),
            invoice.getIssueDate(),
            invoice.getDueDate(),
            invoice.getLineItems().stream()
                .map(LineItemResponse::from)
                .toList()
        );
    }

    // Nested record for line item projection — maps only what the API consumer needs.
    public record LineItemResponse(
            String id,
            String description,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal subtotal
    ) {
        public static LineItemResponse from(InvoiceLineItem item) {
            return new LineItemResponse(
                item.getId(),
                item.getDescription(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.subtotal()
            );
        }
    }
}
