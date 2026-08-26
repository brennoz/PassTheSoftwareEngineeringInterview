package com.interview.ood.q037_payment_gateway.solution;

// Pattern: Port (Hexagonal Architecture) — PaymentService depends on this interface, never on
// a concrete acquirer/network SDK. Swapping providers (or mocking in tests) means implementing
// this interface, not touching PaymentService.
public interface PaymentGateway {
    GatewayResponse authorise(PaymentRequest request);
    GatewayResponse capture(String gatewayTransactionId);
    GatewayResponse voidPayment(String gatewayTransactionId);
    GatewayResponse refund(String gatewayTransactionId, Money amount);
}
