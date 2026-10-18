package com.interview.pairingdrill.q080_live_odds_service.answer;

public class Market {

    private String id;
    private MarketStatus status;

    public Market(String id, MarketStatus status) {
        this.id = id;
        this.status = status;
    }

    public String getId()     { return id; }
    public MarketStatus getStatus() { return status; }
    public void setStatus(MarketStatus status) { this.status = status; }
}
