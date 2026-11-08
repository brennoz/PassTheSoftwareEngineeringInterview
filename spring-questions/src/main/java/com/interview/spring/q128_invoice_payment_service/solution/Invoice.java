package com.interview.spring.q128_invoice_payment_service.solution;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// Aggregate root: owns all state and domain behaviour for an invoice.
//
// Domain methods (issue, applyPayment, cancel, addLineItem) live HERE, not in the service.
// Service's job: load → call domain method → save. No if/else state logic in the service.
// This is the "fat domain model" approach — the entity is smart, the service is thin.
// Alternative (anemic domain): all logic in the service, entity is just a DTO.
// Rejected: anemic model scatters state machine rules across service methods — testing
// the rules requires a Spring context or heavy mocking, not a simple unit test on the entity.
//
// @Entity: maps to the invoices table. JPA handles persistence; domain methods handle behaviour.
// Why separate these concerns? Persistence (annotations) should not constrain domain design.
// A domain method should read as business logic, not as a JPA operation.
//
// @Enumerated(EnumType.STRING): stores "ISSUED" not ordinal 1.
// Ordinal breaks silently when enum constants are reordered — a refactor nightmare in prod.
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    private String id;

    @Column(nullable = false)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    // @OneToMany with CascadeType.ALL: any change to lineItems (add/remove) is persisted
    // when the invoice is saved. orphanRemoval = true: removing from the list deletes the row.
    // @JoinColumn keeps the FK on invoice_line_items.invoice_id — avoids a join table.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "invoice_id")
    private List<InvoiceLineItem> lineItems = new ArrayList<>();

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal paidAmount;

    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    // JPA needs a no-arg constructor; use static factory for application code.
    protected Invoice() {}

    // Static factory: centralises construction, guarantees correct initial state.
    // NOT TO DO: public constructor with all fields — callers could set status = PAID
    // at construction, bypassing the state machine entirely.
    public static Invoice create(String customerId, LocalDate dueDate) {
        Invoice inv       = new Invoice();
        inv.id            = UUID.randomUUID().toString();
        inv.customerId    = customerId;
        inv.status        = InvoiceStatus.DRAFT;
        inv.paidAmount    = BigDecimal.ZERO;
        inv.dueDate       = dueDate;
        return inv;
    }

    // --- Domain methods ---

    public void addLineItem(String description, BigDecimal unitPrice, int quantity) {
        // Guard: only DRAFT invoices can have items added.
        // Issued invoices are a legal document — adding items retroactively is fraud.
        if (status != InvoiceStatus.DRAFT) {
            throw new InvalidStateTransitionException(id, status, InvoiceStatus.DRAFT);
        }
        lineItems.add(InvoiceLineItem.of(description, unitPrice, quantity));
    }

    public void issue() {
        transitionTo(InvoiceStatus.ISSUED);
        this.issueDate = LocalDate.now();
    }

    public void applyPayment(BigDecimal amount) {
        // Guard: only ISSUED, PARTIALLY_PAID, OVERDUE invoices can receive payments.
        if (!status.canTransitionTo(InvoiceStatus.PAID) &&
            !status.canTransitionTo(InvoiceStatus.PARTIALLY_PAID)) {
            throw new InvalidStateTransitionException(id, status, InvoiceStatus.PAID);
        }
        BigDecimal remaining = amountDue();
        if (amount.compareTo(remaining) > 0) {
            // Overpayment: reject. In telecoms billing, overpayments create credit notes —
            // a separate flow. Here we keep it simple: reject with a clear message.
            throw new IllegalArgumentException(
                "Payment " + amount + " exceeds amount due " + remaining);
        }
        this.paidAmount = paidAmount.add(amount);
        // Transition based on whether the invoice is now fully paid.
        if (amountDue().compareTo(BigDecimal.ZERO) == 0) {
            transitionTo(InvoiceStatus.PAID);
        } else {
            // ISSUED → PARTIALLY_PAID; OVERDUE → PARTIALLY_PAID
            if (status == InvoiceStatus.ISSUED || status == InvoiceStatus.OVERDUE) {
                transitionTo(InvoiceStatus.PARTIALLY_PAID);
            }
            // PARTIALLY_PAID stays PARTIALLY_PAID until fully paid — no transition needed
        }
    }

    public void markOverdue() {
        transitionTo(InvoiceStatus.OVERDUE);
    }

    public void cancel() {
        transitionTo(InvoiceStatus.CANCELLED);
    }

    // --- Computed fields ---

    public BigDecimal totalAmount() {
        return lineItems.stream()
            .map(InvoiceLineItem::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal amountDue() {
        return totalAmount().subtract(paidAmount);
    }

    // --- Private helpers ---

    private void transitionTo(InvoiceStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new InvalidStateTransitionException(id, status, next);
        }
        this.status = next;
    }

    // --- Accessors ---

    public String getId()                    { return id; }
    public String getCustomerId()            { return customerId; }
    public InvoiceStatus getStatus()         { return status; }
    public BigDecimal getPaidAmount()        { return paidAmount; }
    public LocalDate getIssueDate()          { return issueDate; }
    public LocalDate getDueDate()            { return dueDate; }
    public List<InvoiceLineItem> getLineItems() {
        return Collections.unmodifiableList(lineItems);
    }
}
