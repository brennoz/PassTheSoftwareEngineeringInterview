package com.interview.pairingdrill.q078_payment_search.solution;

import java.math.BigDecimal;

public class Payment {

    private String paymentId;
    private BigDecimal amount;
    private String status;
    private String type;
    private boolean flaggedForRisk;

    public Payment() {}

    public Payment(String paymentId, BigDecimal amount, String status, String type, boolean flaggedForRisk) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
        this.type = type;
        this.flaggedForRisk = flaggedForRisk;
    }

    public String getPaymentId() { return paymentId; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public boolean isFlaggedForRisk() { return flaggedForRisk; }
}
