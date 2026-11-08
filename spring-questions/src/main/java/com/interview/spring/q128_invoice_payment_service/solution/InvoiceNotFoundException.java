package com.interview.spring.q128_invoice_payment_service.solution;

// GlobalExceptionHandler maps this to HTTP 404. Unchecked — every caller that loads
// an invoice goes through InvoiceService.findOrThrow(), so there is no scenario where
// a caller would want to handle "not found" and continue; propagating to the handler is correct.
public class InvoiceNotFoundException extends RuntimeException {

    public InvoiceNotFoundException(String id) {
        super("Invoice not found: " + id);
    }
}
