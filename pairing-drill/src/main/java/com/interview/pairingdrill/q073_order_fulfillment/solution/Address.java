package com.interview.pairingdrill.q073_order_fulfillment.solution;

public class Address {

    private String countryCode;

    public Address() {
    }

    public Address(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    // Why here: Address owns countryCode, so it's the right place to own the definition of
    // "international." The service used to compare the raw string itself — moving the
    // comparison here means the definition only ever needs to change in one place.
    public boolean isInternational() {
        return "INTERNATIONAL".equals(countryCode);
    }
}
