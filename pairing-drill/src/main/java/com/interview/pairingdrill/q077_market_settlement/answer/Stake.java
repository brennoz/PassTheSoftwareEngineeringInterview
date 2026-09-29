package com.interview.pairingdrill.q077_market_settlement.answer;

import java.math.BigDecimal;
import java.util.Currency;

public class Stake {

    private BigDecimal amount;
    private Currency currency;

    public Stake() {
    }

    public Stake(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
