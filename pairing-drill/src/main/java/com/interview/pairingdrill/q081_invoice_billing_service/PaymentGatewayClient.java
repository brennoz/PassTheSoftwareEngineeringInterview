package com.interview.pairingdrill.q081_invoice_billing_service;

public interface PaymentGatewayClient {
    void charge(String invoiceId, double amount) throws Exception;
}
