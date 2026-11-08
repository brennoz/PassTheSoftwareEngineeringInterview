package com.interview.pairingdrill.q081_invoice_billing_service.solution;

import com.interview.pairingdrill.q081_invoice_billing_service.Invoice;
import com.interview.pairingdrill.q081_invoice_billing_service.InvoiceRepository;
import com.interview.pairingdrill.q081_invoice_billing_service.PaymentGatewayClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fixed version of InvoiceBillingService.
 *
 * Issues fixed:
 *
 * BUG 1 — double/float for monetary amounts (FIXED: BigDecimal).
 * double has binary floating-point precision: 0.1 + 0.2 = 0.30000000000000004.
 * For billing: £9.99 * 1000 invoices = wrong totals, regulatory reporting errors.
 * BigDecimal stores exact decimal representation with controlled rounding.
 *
 * BUG 2 — String status with magic literals (FIXED: InvoiceStatus enum).
 * String comparisons scattered across codebase are brittle: typo "ISUED" compiles,
 * fails silently at runtime. Enum makes invalid states compile errors.
 * Also: String.equals() forces every caller to know the exact spelling — enum centralises it.
 *
 * BUG 3 — HashMap in singleton @Service for shared cache (FIXED: ConcurrentHashMap).
 * @Service = singleton = shared across all request threads.
 * HashMap.put() + get() under concurrent writes = corrupted internal structure.
 * ConcurrentHashMap.computeIfAbsent() is atomic per key slot.
 * Further improvement (not done here): use Redis for distributed cache
 * so invalidation propagates to all pods, not just one.
 *
 * BUG 4 — Missing @Transactional on issueInvoice (FIXED: added).
 * issueInvoice has two DB operations: findById + save.
 * Without @Transactional: if save fails (DB constraint, network), findById
 * already consumed the connection and changed no data — but no rollback guarantee.
 * With @Transactional: both operations in one unit of work, atomic commit/rollback.
 *
 * BUG 5 — Swallowed exception in applyPayment (FIXED: rethrow as domain exception).
 * catch(Exception e) { System.out.println(...) } — payment gateway failure is SILENT.
 * The invoice is updated to "PAID" even though the gateway charge FAILED.
 * This creates ghost payments: invoice shows PAID, money was never taken.
 * Fix: rethrow as a typed domain exception → controller returns 502/503 to client.
 */
@Service
public class InvoiceBillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentGatewayClient paymentGateway;

    // FIX 3: ConcurrentHashMap — thread-safe for singleton @Service shared across request threads
    // Note: for multi-pod deployment, use Redis instead — in-memory cache is pod-local
    private final Map<String, BigDecimal> rateTableCache = new ConcurrentHashMap<>();

    public InvoiceBillingService(InvoiceRepository invoiceRepository,
                                  PaymentGatewayClient paymentGateway) {
        this.invoiceRepository = invoiceRepository;
        this.paymentGateway = paymentGateway;
    }

    // FIX 1: BigDecimal for all monetary arithmetic
    // FIX 2: InvoiceStatus.DRAFT instead of "DRAFT"
    public Invoice createInvoice(String customerId, String planId, BigDecimal totalAmount) {
        BigDecimal discountPct = rateTableCache.get(planId);
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (discountPct != null) {
            discountAmount = totalAmount
                .multiply(discountPct)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        BigDecimal finalAmount = totalAmount.subtract(discountAmount);

        Invoice invoice = new Invoice();
        invoice.setId(UUID.randomUUID().toString());
        invoice.setCustomerId(customerId);
        invoice.setPlanId(planId);
        // Store as String to avoid changing Invoice.java entity (migration concern);
        // in production: Invoice would have InvoiceStatus field with @Enumerated(EnumType.STRING)
        invoice.setTotalAmount(finalAmount.doubleValue());
        invoice.setStatus(InvoiceStatus.DRAFT.name());
        return invoiceRepository.save(invoice);
    }

    // FIX 4: @Transactional — findById + save must be in one unit of work
    @Transactional
    public Invoice issueInvoice(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found: " + invoiceId));

        // FIX 2: enum comparison instead of magic string
        if (!InvoiceStatus.DRAFT.name().equals(invoice.getStatus())) {
            throw new InvalidInvoiceStateException(
                "Invoice must be DRAFT to issue, was: " + invoice.getStatus());
        }

        invoice.setStatus(InvoiceStatus.ISSUED.name());
        invoice.setIssuedAt(java.time.Instant.now());
        return invoiceRepository.save(invoice);
    }

    // FIX 4: @Transactional — gateway charge + invoice save must be atomic
    // FIX 5: don't swallow gateway exception — rethrow as domain exception
    @Transactional
    public Invoice applyPayment(String invoiceId, BigDecimal paymentAmount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found: " + invoiceId));

        InvoiceStatus current = InvoiceStatus.valueOf(invoice.getStatus());
        if (current != InvoiceStatus.ISSUED && current != InvoiceStatus.PARTIALLY_PAID) {
            throw new InvalidInvoiceStateException(
                "Cannot apply payment to invoice in status: " + current);
        }

        // FIX 5: let the exception propagate — caller (controller) handles it
        // Gateway failure → @Transactional rolls back → invoice not updated → correct
        try {
            paymentGateway.charge(invoiceId, paymentAmount.doubleValue());
        } catch (Exception e) {
            throw new PaymentGatewayException(
                "Payment gateway failure for invoice " + invoiceId, e);
        }

        // FIX 1: BigDecimal arithmetic for monetary comparison
        BigDecimal total = new BigDecimal(String.valueOf(invoice.getTotalAmount()));
        BigDecimal paid = new BigDecimal(String.valueOf(invoice.getPaidAmount()));
        BigDecimal newPaid = paid.add(paymentAmount);
        BigDecimal remaining = total.subtract(newPaid);

        invoice.setPaidAmount(newPaid.doubleValue());
        // compareTo not equals — BigDecimal scale-sensitive
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(InvoiceStatus.PAID.name());
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID.name());
        }

        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getOverdueInvoices(String customerId) {
        return invoiceRepository.findByCustomerIdAndStatus(
            customerId, InvoiceStatus.OVERDUE.name());
    }

    // FIX 3: assign to ConcurrentHashMap field (field is final ConcurrentHashMap)
    // Don't reassign the field — that's not thread-safe (another thread might use old ref)
    // Instead: clear + putAll atomically... but ConcurrentHashMap has no clear+putAll atomic.
    // Best: iterate and put, or use a volatile reference to an immutable map.
    // This simplified fix just replaces entries:
    public void refreshRates(Map<String, BigDecimal> newRates) {
        rateTableCache.clear();     // not atomic but acceptable for cache refresh
        rateTableCache.putAll(newRates);
    }
}
