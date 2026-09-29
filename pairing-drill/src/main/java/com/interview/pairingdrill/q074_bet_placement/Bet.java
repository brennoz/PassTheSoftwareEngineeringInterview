package com.interview.pairingdrill.q074_bet_placement;

import java.math.BigDecimal;

public class Bet {

    private String id;
    private BigDecimal stake;
    private BigDecimal odds;

    public Bet() {
    }

    public Bet(String id, BigDecimal stake, BigDecimal odds) {
        this.id = id;
        this.stake = stake;
        this.odds = odds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getStake() {
        return stake;
    }

    public void setStake(BigDecimal stake) {
        this.stake = stake;
    }

    public BigDecimal getOdds() {
        return odds;
    }

    public void setOdds(BigDecimal odds) {
        this.odds = odds;
    }
}
