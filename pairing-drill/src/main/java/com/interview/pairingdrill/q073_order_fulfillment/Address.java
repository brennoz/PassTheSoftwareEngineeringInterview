package com.interview.pairingdrill.q073_order_fulfillment;

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
}
