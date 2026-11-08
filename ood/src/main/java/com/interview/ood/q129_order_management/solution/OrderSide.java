package com.interview.ood.q129_order_management.solution;

// FIX tag 54 — Side. Stored as enum rather than String so every call site is a compile-time
// constant, not a string comparison. What NOT to do: accept "BUY"/"SELL" as raw strings —
// a typo silently routes the wrong direction, which in a trading system means a loss.
public enum OrderSide {
    BUY("1"), SELL("2");

    private final String fixValue;

    OrderSide(String fixValue) { this.fixValue = fixValue; }

    public String getFixValue() { return fixValue; }
}
