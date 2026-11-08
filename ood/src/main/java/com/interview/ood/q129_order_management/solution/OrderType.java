package com.interview.ood.q129_order_management.solution;

// Data-carrying enum: stores the FIX tag 40 (OrdType) value so OrderRoutingStrategy
// can produce a valid FIX NewOrderSingle without a separate lookup table.
public enum OrderType {
    MARKET("1"),   // FIX OrdType=1 — execute immediately at best available price; no limitPrice
    LIMIT("2"),    // FIX OrdType=2 — execute only at limitPrice or better
    STOP("3");     // FIX OrdType=3 — becomes a MARKET order when the stop price is triggered

    private final String fixOrdType;

    OrderType(String fixOrdType) { this.fixOrdType = fixOrdType; }

    public String getFixOrdType() { return fixOrdType; }
}
