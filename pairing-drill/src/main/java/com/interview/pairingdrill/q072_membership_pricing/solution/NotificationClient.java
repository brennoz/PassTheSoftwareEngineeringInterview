package com.interview.pairingdrill.q072_membership_pricing.solution;

public interface NotificationClient {

    void sendWelcomeEmail(String email);

    void sendRenewalEmail(String email);
}
