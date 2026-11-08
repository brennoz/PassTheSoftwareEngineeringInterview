package com.interview.spring.q128_invoice_payment_service.solution;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

// @Entity with its own ID — allows InvoiceRepository's findById to load the invoice
// with its line items via @OneToMany join, without a separate repository for line items.
// Line items have no lifecycle outside their invoice: they're created and deleted
// with the invoice (cascade = ALL, orphanRemoval = true on the parent side).
//
// Alternative: @Embeddable + @ElementCollection on Invoice.
// Rejected: ElementCollection creates its own join table but with no @Id column —
// querying or deleting individual line items requires loading the whole collection.
// Separate @Entity gives each line item a PK, making individual operations possible
// and the schema more transparent for SQL queries.
@Entity
@Table(name = "invoice_line_items")
public class InvoiceLineItem {

    @Id
    private String id;
    private String description;
    private BigDecimal unitPrice;
    private int quantity;

    // JPA requires a no-arg constructor. Protected prevents direct use outside the package
    // but satisfies Hibernate's reflection-based instantiation.
    protected InvoiceLineItem() {}

    public static InvoiceLineItem of(String description, BigDecimal unitPrice, int quantity) {
        InvoiceLineItem item = new InvoiceLineItem();
        item.id          = UUID.randomUUID().toString();
        item.description = description;
        item.unitPrice   = unitPrice;
        item.quantity    = quantity;
        return item;
    }

    // BigDecimal.valueOf(quantity) not new BigDecimal(quantity) — both are fine for int,
    // but valueOf is consistent with how monetary values are handled throughout.
    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getId()           { return id; }
    public String getDescription()  { return description; }
    public BigDecimal getUnitPrice(){ return unitPrice; }
    public int getQuantity()        { return quantity; }
}
