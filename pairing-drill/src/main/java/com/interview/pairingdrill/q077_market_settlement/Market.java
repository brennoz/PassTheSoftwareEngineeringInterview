package com.interview.pairingdrill.q077_market_settlement;

public class Market {

    private String marketId;
    private String status;
    private double totalPayout;

    public Market() {
    }

    public Market(String marketId, String status) {
        this.marketId = marketId;
        this.status = status;
    }

    public String getMarketId() { return marketId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalPayout() { return totalPayout; }
    public void setTotalPayout(double totalPayout) { this.totalPayout = totalPayout; }
}
