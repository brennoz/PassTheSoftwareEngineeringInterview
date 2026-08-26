package com.interview.ood.q037_payment_gateway.solution;

import java.util.Optional;
import java.util.UUID;

// Pattern: Service Layer — orchestrates PaymentGateway (external call) + PaymentRepository
// (persistence) + PaymentStatus (state-machine guard) per use case; keeps that coordination out
// of the domain records themselves.
//
// Why the constructor is typed to InMemoryPaymentRepository, not PaymentRepository: initiate()
// needs findByMerchantReference() for idempotency, and that method isn't on the interface (see
// InMemoryPaymentRepository's comment for why). What NOT to do: cast the interface down to the
// concrete type inside initiate() — that hides the real dependency behind a type that claims to
// be swappable but isn't; typing the field/constructor honestly is the same coupling made
// visible instead of hidden behind a runtime cast that could ClassCastException with a future
// adapter.
public class PaymentService {

    private final PaymentGateway gateway;
    private final InMemoryPaymentRepository repository;

    PaymentService(PaymentGateway gateway, InMemoryPaymentRepository repository) {
        this.gateway = gateway;
        this.repository = repository;
    }

    // Idempotency: a client retrying a timed-out request sends the same merchantReference.
    // Returning the existing Payment instead of creating a second one is what prevents a
    // duplicate authorisation/charge on retry.
    public Payment initiate(PaymentRequest request) {
        Optional<Payment> existing = repository.findByMerchantReference(request.merchantReference());
        if (existing.isPresent()) {
            return existing.get();
        }
        Payment payment = new Payment(UUID.randomUUID().toString(), request, PaymentStatus.INITIATED, null);
        repository.save(payment);
        return payment;
    }

    public Payment authorise(String paymentId) {
        Payment payment = findOrThrow(paymentId);
        payment.status().assertCanTransitionTo(PaymentStatus.AUTHORISED);
        GatewayResponse response = gateway.authorise(payment.request());
        PaymentStatus next = response.success() ? PaymentStatus.AUTHORISED : PaymentStatus.FAILED;
        return repository.updateStatus(paymentId, next, response.gatewayTransactionId());
    }

    public Payment capture(String paymentId) {
        Payment payment = findOrThrow(paymentId);
        payment.status().assertCanTransitionTo(PaymentStatus.CAPTURED);
        GatewayResponse response = gateway.capture(payment.gatewayTransactionId());
        PaymentStatus next = response.success() ? PaymentStatus.CAPTURED : payment.status();
        return repository.updateStatus(paymentId, next, payment.gatewayTransactionId());
    }

    public Payment voidPayment(String paymentId) {
        Payment payment = findOrThrow(paymentId);
        payment.status().assertCanTransitionTo(PaymentStatus.VOIDED);
        GatewayResponse response = gateway.voidPayment(payment.gatewayTransactionId());
        PaymentStatus next = response.success() ? PaymentStatus.VOIDED : payment.status();
        return repository.updateStatus(paymentId, next, payment.gatewayTransactionId());
    }

    public Payment refund(String paymentId, Money amount) {
        Payment payment = findOrThrow(paymentId);
        payment.status().assertCanTransitionTo(PaymentStatus.REFUNDED);
        GatewayResponse response = gateway.refund(payment.gatewayTransactionId(), amount);
        PaymentStatus next = response.success() ? PaymentStatus.REFUNDED : payment.status();
        return repository.updateStatus(paymentId, next, payment.gatewayTransactionId());
    }

    public Payment findOrThrow(String paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
    }
}
