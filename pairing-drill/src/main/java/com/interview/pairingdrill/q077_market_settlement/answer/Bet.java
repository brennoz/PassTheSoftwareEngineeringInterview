package com.interview.pairingdrill.q077_market_settlement.answer;

import java.math.BigDecimal;

public class Bet {

    private String betId;
    private String marketId;
    private BettingSide side;
    private BigDecimal odds;
    private Stake stake;
    private boolean settled;
    private boolean won;

    public Bet() {
    }

    public Bet(String betId, String marketId, BettingSide side, BigDecimal odds, Stake stake) {
        this.betId = betId;
        this.marketId = marketId;
        this.side = side;
        this.odds = odds;
        this.stake = stake;
    }

    public String getBetId() { return betId; }
    public String getMarketId() { return marketId; }
    public BettingSide getSide() { return side; }
    public BigDecimal getOdds() { return odds; }
    public Stake getStake() { return stake; }
    public boolean isSettled() { return settled; }
    public void setSettled(boolean settled) { this.settled = settled; }
    public boolean isWon() { return won; }
    public void setWon(boolean won) { this.won = won; }
}
