package com.interview.pairingdrill.q078_payment_search.answer;

public class StatusSpec implements PaymentSpec {

    private final PaymentStatus status;

public StatusSpec(PaymentStatus status) {
        this.status = status;
    }

    @Override
    public boolean isSatisfiedBy(Payment p) {
        return status.equals(p.getStatus());
    }
}
