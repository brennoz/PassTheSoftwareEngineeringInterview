package com.interview.pairingdrill.q077_market_settlement;

public class Stake {

    private double amount;
    private String currency;

    public Stake() {
    }

    public Stake(double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
