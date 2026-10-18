package com.interview.pairingdrill.q078_payment_search.solution;

public class TypeSpec implements PaymentSpec {

    private final String type;

    public TypeSpec(String type) {
        this.type = type;
    }

    @Override
    public boolean isSatisfiedBy(Payment p) {
        return type.equals(p.getType());
    }
}
