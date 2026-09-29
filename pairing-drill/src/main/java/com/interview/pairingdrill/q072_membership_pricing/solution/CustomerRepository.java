package com.interview.pairingdrill.q072_membership_pricing.solution;

public interface CustomerRepository {

    void save(Customer customer);

    // Why: replaces the seeded private static int activeMembershipCount field. That field was
    // write-only (nothing read it), not thread-safe, and leaked state across test runs because
    // it never reset. The count already exists in the data — deriving it here removes the bug
    // class entirely instead of just making the old field thread-safe.
    int countByMembershipStatus(String status);
}
