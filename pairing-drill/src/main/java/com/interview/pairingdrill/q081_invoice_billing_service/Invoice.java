package com.interview.pairingdrill.q081_invoice_billing_service;

import java.time.Instant;

public class Invoice {

    private String id;
    private String customerId;
    private String planId;

    // BUG 1: double used for monetary amounts — precision loss risk
    private double totalAmount;
    private double paidAmount;

    // BUG 2: status as raw String — magic literal comparisons scattered in service
    private String status;

    private Instant issuedAt;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }
}
