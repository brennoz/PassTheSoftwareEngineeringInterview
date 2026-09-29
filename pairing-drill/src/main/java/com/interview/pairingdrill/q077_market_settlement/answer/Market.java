package com.interview.pairingdrill.q077_market_settlement.answer;

import java.math.BigDecimal;

public class Market {

    private String marketId;
    private MarketStatus status;
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
