package com.interview.pairingdrill.q078_payment_search.answer;

public class FlaggedForRiskSpec implements PaymentSpec {

    @Override
    public boolean isSatisfiedBy(Payment p) {
        return p.isFlaggedForRisk();
    }
}
