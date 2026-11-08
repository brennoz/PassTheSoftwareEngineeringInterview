package com.interview.ood.q129_order_management.solution;

// FIX tag 59 — TimeInForce controls how long an order remains active.
// Storing the FIX wire value directly avoids a translation step in the venue adapter.
public enum TimeInForce {
    DAY("0"),  // cancel any unexecuted quantity at market close
    GTC("1"),  // Good Till Cancelled — persists across sessions until filled or explicitly cancelled
    IOC("3"),  // Immediate Or Cancel — fill whatever is available now, cancel the rest
    FOK("4");  // Fill Or Kill — fill entire quantity immediately or cancel entirely

    private final String fixValue;

    TimeInForce(String fixValue) { this.fixValue = fixValue; }

    public String getFixValue() { return fixValue; }
}
