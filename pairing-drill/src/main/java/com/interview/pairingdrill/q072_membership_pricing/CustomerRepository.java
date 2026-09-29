package com.interview.pairingdrill.q072_membership_pricing;

public interface CustomerRepository {

    void save(Customer customer);
    int countByMembershipStatus(String status);
}
