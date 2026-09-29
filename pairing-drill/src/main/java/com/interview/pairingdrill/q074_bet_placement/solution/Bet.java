package com.interview.pairingdrill.q074_bet_placement.solution;

import java.math.BigDecimal;

public class Bet {
    private String id;
    private BigDecimal stake;
    private BigDecimal odds;

    public Bet() {}
    public Bet(String id, BigDecimal stake, BigDecimal odds) { this.id = id; this.stake = stake; this.odds = odds; }
    public String getId() { return id; }
    public BigDecimal getStake() { return stake; }
    public BigDecimal getOdds() { return odds; }
}
