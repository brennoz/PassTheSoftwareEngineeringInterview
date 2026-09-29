package com.interview.pairingdrill.q077_market_settlement.solution;

import java.math.BigDecimal;

public class Market {

    private String marketId;
    // Why MarketStatus not String: see MarketStatus.java.
    private MarketStatus status;
    // Why BigDecimal not double: totalPayout is a derived sum of BigDecimal values —
    // storing the result as double would throw away the precision gained upstream.
    private BigDecimal totalPayout;

    public Market() {
    }

    public Market(String marketId, MarketStatus status) {
        this.marketId = marketId;
        this.status = status;
    }

    public String getMarketId() { return marketId; }
    public MarketStatus getStatus() { return status; }
    public void setStatus(MarketStatus status) { this.status = status; }
    public BigDecimal getTotalPayout() { return totalPayout; }
    public void setTotalPayout(BigDecimal totalPayout) { this.totalPayout = totalPayout; }
}
