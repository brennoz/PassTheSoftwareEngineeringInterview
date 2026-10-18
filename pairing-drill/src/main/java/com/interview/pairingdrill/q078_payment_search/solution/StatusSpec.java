package com.interview.pairingdrill.q078_payment_search.solution;

public class StatusSpec implements PaymentSpec {

    private final String status;

    public StatusSpec(String status) {
        this.status = status;
    }

    @Override
    public boolean isSatisfiedBy(Payment p) {
        return status.equals(p.getStatus());
    }
}
