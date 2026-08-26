package com.interview.ood.q037_payment_gateway.solution;

import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(String id);
    Payment updateStatus(String id, PaymentStatus status, String gatewayTxId);
}
