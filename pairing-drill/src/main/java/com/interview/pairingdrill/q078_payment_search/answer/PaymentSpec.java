package com.interview.pairingdrill.q078_payment_search.answer;

@FunctionalInterface
public interface PaymentSpec {

    boolean isSatisfiedBy(Payment p);

    default PaymentSpec and(PaymentSpec other) {
        return p -> this.isSatisfiedBy(p) && other.isSatisfiedBy(p);
    }

    default PaymentSpec or(PaymentSpec other) {
        return p -> this.isSatisfiedBy(p) || other.isSatisfiedBy(p);
    }

    default PaymentSpec negate() {
        return p -> !this.isSatisfiedBy(p);
    }

    static PaymentSpec any() {
        return p -> true;
    }
}
