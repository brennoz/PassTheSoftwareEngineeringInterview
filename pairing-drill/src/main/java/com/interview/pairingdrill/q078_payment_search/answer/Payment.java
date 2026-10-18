package com.interview.pairingdrill.q078_payment_search.answer;

import java.math.BigDecimal;

public class Payment {

    private String paymentId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentType type;
    private boolean flaggedForRisk;

    public Payment() {}

    public Payment(String paymentId, BigDecimal amount, PaymentStatus status, PaymentType type, boolean flaggedForRisk) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
        this.type = type;
        this.flaggedForRisk = flaggedForRisk;
    }

    public String getPaymentId() { return paymentId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public PaymentType getType() { return type; }
    public boolean isFlaggedForRisk() { return flaggedForRisk; }
}
