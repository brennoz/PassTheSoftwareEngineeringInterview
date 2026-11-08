package com.interview.pairingdrill.q081_invoice_billing_service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Core billing service: creates, issues, and applies payments to invoices.
 *
 * Written by Alex (junior dev). Passes all current unit tests.
 * Review before merge.
 */
@Service
public class InvoiceBillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentGatewayClient paymentGateway;

    // Rate table cache: planId -> discount percentage
    // Loaded once on first use, refreshed manually via refreshRates()
    private Map<String, Double> rateTableCache = new HashMap<>();

    public InvoiceBillingService(InvoiceRepository invoiceRepository,
                                  PaymentGatewayClient paymentGateway) {
        this.invoiceRepository = invoiceRepository;
        this.paymentGateway = paymentGateway;
    }

    /**
     * Creates a new draft invoice for the given customer and plan.
     */
    public Invoice createInvoice(String customerId, String planId, double totalAmount) {
        Double discountPct = rateTableCache.get(planId);
        double discountAmount = 0.0;
        if (discountPct != null) {
            discountAmount = totalAmount * (discountPct / 100.0);
        }
        double finalAmount = totalAmount - discountAmount;

        Invoice invoice = new Invoice();
        invoice.setId(UUID.randomUUID().toString());
        invoice.setCustomerId(customerId);
        invoice.setPlanId(planId);
        invoice.setTotalAmount(finalAmount);
        invoice.setStatus("DRAFT");
        return invoiceRepository.save(invoice);
    }

    /**
     * Issues a DRAFT invoice (changes status to ISSUED and records issue date).
     */
    public Invoice issueInvoice(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        if (!invoice.getStatus().equals("DRAFT")) {
            throw new RuntimeException("Invoice must be in DRAFT status to issue");
        }

        invoice.setStatus("ISSUED");
        invoice.setIssuedAt(java.time.Instant.now());
        return invoiceRepository.save(invoice);
    }

    /**
     * Applies a payment to an ISSUED invoice. Calls the payment gateway,
     * then updates the invoice status.
     */
    public Invoice applyPayment(String invoiceId, double paymentAmount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        if (!invoice.getStatus().equals("ISSUED") &&
            !invoice.getStatus().equals("PARTIALLY_PAID")) {
            throw new RuntimeException("Cannot apply payment to invoice in status: "
                + invoice.getStatus());
        }

        try {
            paymentGateway.charge(invoiceId, paymentAmount);
        } catch (Exception e) {
            // Gateway failed - just log and continue
            System.out.println("Payment gateway error: " + e.getMessage());
        }

        double remaining = invoice.getTotalAmount() - paymentAmount;
        invoice.setPaidAmount(invoice.getPaidAmount() + paymentAmount);

        if (remaining <= 0) {
            invoice.setStatus("PAID");
        } else {
            invoice.setStatus("PARTIALLY_PAID");
        }

        return invoiceRepository.save(invoice);
    }

    /**
     * Returns all overdue invoices for a customer.
     */
    public List<Invoice> getOverdueInvoices(String customerId) {
        return invoiceRepository.findByCustomerIdAndStatus(customerId, "OVERDUE");
    }

    /**
     * Refreshes the rate table cache from the database.
     */
    public void refreshRates(Map<String, Double> newRates) {
        this.rateTableCache = new HashMap<>(newRates);
    }
}
