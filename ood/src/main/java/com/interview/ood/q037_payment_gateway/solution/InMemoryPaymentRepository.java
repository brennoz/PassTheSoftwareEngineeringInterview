package com.interview.ood.q037_payment_gateway.solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Pattern: Repository (in-memory adapter) — implements the PaymentRepository port for tests
// and local demos. findByMerchantReference is NOT on the interface: it's an idempotency-lookup
// detail specific to this adapter's storage shape (a HashMap scan), not a general repository
// operation every implementation must support. Alternative rejected: putting
// findByMerchantReference on PaymentRepository would force every future adapter (JPA, DynamoDB)
// to implement a merchant-reference index even if it doesn't need one for idempotency (e.g. a
// DB adapter might enforce idempotency via a unique constraint + catch instead).
public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> store = new HashMap<>();

    @Override
    public void save(Payment payment) {
        store.put(payment.id(), payment);
    }

    @Override
    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Payment updateStatus(String id, PaymentStatus status, String gatewayTxId) {
        Payment existing = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
        Payment updated = existing.withStatus(status).withGatewayTxId(gatewayTxId);
        store.put(id, updated);
        return updated;
    }

    public Optional<Payment> findByMerchantReference(String merchantReference) {
        return store.values().stream()
                .filter(payment -> merchantReference.equals(payment.request().merchantReference()))
                .findFirst();
    }
}
