package com.interview.spring.q128_invoice_payment_service.solution;

// Unchecked — callers guard with canTransitionTo() before calling state-change methods.
// GlobalExceptionHandler maps this to HTTP 409 Conflict: the request is valid but conflicts
// with the current resource state (can't pay a CANCELLED invoice).
// 409 not 400: the input is well-formed — the problem is resource state, not request format.
public class InvalidStateTransitionException extends RuntimeException {

    public InvalidStateTransitionException(String invoiceId, InvoiceStatus from, InvoiceStatus to) {
        super("Invoice " + invoiceId + ": illegal transition " + from + " → " + to);
    }
}
