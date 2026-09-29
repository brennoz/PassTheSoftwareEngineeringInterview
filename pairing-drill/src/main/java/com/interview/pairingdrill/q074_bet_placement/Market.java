package com.interview.pairingdrill.q074_bet_placement;

public class Market {

    private String id;
    private String status;

    public Market() {
    }

    public Market(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
