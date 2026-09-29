package com.interview.pairingdrill.q072_membership_pricing.solution;

public class BillingAccount {

    private String cardToken;
    private String contactEmail;

    public BillingAccount() {
    }

    public BillingAccount(String cardToken, String contactEmail) {
        this.cardToken = cardToken;
        this.contactEmail = contactEmail;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
