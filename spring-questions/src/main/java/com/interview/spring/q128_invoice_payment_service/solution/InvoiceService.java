package com.interview.spring.q128_invoice_payment_service.solution;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// @Service: marks this as a Spring bean, triggers Spring proxy creation.
// The proxy is what makes @Transactional work — Spring wraps the bean in a proxy that
// starts/commits/rolls back a transaction around the method.
//
// @Transactional on the class: applies to all public methods as the default.
// readOnly = true overrides on specific methods: hint to JPA/Hibernate to skip dirty-checking
// (no need to track entity changes for a read-only method), and signals to the DB driver that
// a read replica may be used — performance benefit at scale.
//
// Business rules live in Invoice (the domain object), NOT here.
// This service: load → delegate → save. No if/else logic about state transitions.
// Why? Keeps transaction management (infrastructure) separate from domain rules (logic).
// A service packed with if/else state logic is an anemic domain model — the service becomes
// a god class that's hard to test without setting up the full application.
//
// Constructor injection (not @Autowired field injection):
// Field injection hides dependencies — you can't see what InvoiceService needs without
// reading every field. Constructor injection makes dependencies explicit, and allows
// new InvoiceService(mockRepo) in tests without starting a Spring context.
@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository repository;

    public InvoiceService(InvoiceRepository repository) {
        this.repository = repository;
    }

    public Invoice createInvoice(String customerId, LocalDate dueDate) {
        Invoice invoice = Invoice.create(customerId, dueDate);
        return repository.save(invoice);
    }

    public Invoice addLineItem(String invoiceId, String description,
                               BigDecimal unitPrice, int quantity) {
        Invoice invoice = findOrThrow(invoiceId);
        invoice.addLineItem(description, unitPrice, quantity);
        return repository.save(invoice);
    }

    public Invoice issueInvoice(String invoiceId) {
        Invoice invoice = findOrThrow(invoiceId);
        invoice.issue();
        return repository.save(invoice);
    }

    public Invoice recordPayment(String invoiceId, BigDecimal amount) {
        Invoice invoice = findOrThrow(invoiceId);
        invoice.applyPayment(amount);
        return repository.save(invoice);
    }

    public Invoice cancelInvoice(String invoiceId) {
        Invoice invoice = findOrThrow(invoiceId);
        invoice.cancel();
        return repository.save(invoice);
    }

    public void deleteInvoice(String invoiceId) {
        Invoice invoice = findOrThrow(invoiceId);
        // Only DRAFT invoices can be hard-deleted. Issued/Paid invoices are financial records
        // and must not be destroyed — cancel them instead.
        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvalidStateTransitionException(
                invoiceId, invoice.getStatus(), InvoiceStatus.CANCELLED);
        }
        repository.delete(invoice);
    }

    // readOnly = true: Hibernate skips dirty-checking (no change tracking overhead).
    // At scale, this signals to a connection pool or read replica that reads can be served
    // without write-lock contention.
    @Transactional(readOnly = true)
    public Invoice getInvoice(String invoiceId) {
        return findOrThrow(invoiceId);
    }

    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesForCustomer(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    // Centralises "load or 404" — every method uses this instead of duplicating
    // the orElseThrow pattern. One place to change if InvoiceNotFoundException changes.
    private Invoice findOrThrow(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new InvoiceNotFoundException(id));
    }
}
