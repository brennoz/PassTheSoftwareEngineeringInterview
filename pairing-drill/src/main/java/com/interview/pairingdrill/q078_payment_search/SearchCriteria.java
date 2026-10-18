package com.interview.pairingdrill.q078_payment_search;

public class SearchCriteria {

    private double minAmount;
    private double maxAmount;
    private String status;
    private String type;

    public SearchCriteria() {}

    public double getMinAmount() { return minAmount; }
    public void setMinAmount(double minAmount) { this.minAmount = minAmount; }

    public double getMaxAmount() { return maxAmount; }
    public void setMaxAmount(double maxAmount) { this.maxAmount = maxAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
