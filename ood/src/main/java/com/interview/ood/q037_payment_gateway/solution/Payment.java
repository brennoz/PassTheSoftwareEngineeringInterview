package com.interview.ood.q037_payment_gateway.solution;

// Pattern: Value Object with "wither" methods — Payment is immutable, so a status/gatewayTxId
// change produces a new Payment rather than mutating one in place. This is what makes
// InMemoryPaymentRepository.updateStatus() safe to call from concurrent contexts without extra
// locking on the Payment itself: nobody holds a reference to a Payment that changes under them.
public record Payment(String id, PaymentRequest request, PaymentStatus status, String gatewayTransactionId) {

    public Payment withStatus(PaymentStatus status) {
        return new Payment(id, request, status, gatewayTransactionId);
    }

    public Payment withGatewayTxId(String gatewayTransactionId) {
        return new Payment(id, request, status, gatewayTransactionId);
    }
}
