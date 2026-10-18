package com.interview.pairingdrill.q078_payment_search.answer;

public class TypeSpec implements PaymentSpec {

    private final PaymentType type;

    public TypeSpec(PaymentType type) {
        this.type = type;
    }

    @Override
    public boolean isSatisfiedBy(Payment p) {
        return type.equals(p.getType());
    }
}
