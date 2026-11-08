package com.interview.pairingdrill.q081_invoice_billing_service.solution;

public class InvalidInvoiceStateException extends RuntimeException {
    public InvalidInvoiceStateException(String message) { super(message); }
}
