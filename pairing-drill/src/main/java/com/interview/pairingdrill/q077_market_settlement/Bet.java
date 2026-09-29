package com.interview.pairingdrill.q077_market_settlement;

public class Bet {

    private String betId;
    private String marketId;
    private String side;
    private double odds;
    private Stake stake;
    private boolean settled;
    private boolean won;

    public Bet() {
    }

    public Bet(String betId, String marketId, String side, double odds, Stake stake) {
        this.betId = betId;
        this.marketId = marketId;
        this.side = side;
        this.odds = odds;
        this.stake = stake;
    }

    public String getBetId() { return betId; }
    public String getMarketId() { return marketId; }
    public String getSide() { return side; }
    public double getOdds() { return odds; }
    public Stake getStake() { return stake; }
    public boolean isSettled() { return settled; }
    public void setSettled(boolean settled) { this.settled = settled; }
    public boolean isWon() { return won; }
    public void setWon(boolean won) { this.won = won; }
}
