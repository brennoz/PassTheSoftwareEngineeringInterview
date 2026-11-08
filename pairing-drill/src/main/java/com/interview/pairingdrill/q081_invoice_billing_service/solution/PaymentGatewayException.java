package com.interview.pairingdrill.q081_invoice_billing_service.solution;

public class PaymentGatewayException extends RuntimeException {
    public PaymentGatewayException(String message, Throwable cause) { super(message, cause); }
}
