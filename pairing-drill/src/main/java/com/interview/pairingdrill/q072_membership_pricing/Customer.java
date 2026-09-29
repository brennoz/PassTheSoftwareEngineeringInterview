package com.interview.pairingdrill.q072_membership_pricing;

public class Customer {

    private Long id;
    private MembershipTier membershipTier;
    private String membershipStatus;
    private BillingAccount billingAccount;

    public Customer() {
    }

    public Customer(Long id, BillingAccount billingAccount) {
        this.id = id;
        this.billingAccount = billingAccount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MembershipTier getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(MembershipTier membershipTier) {
        this.membershipTier = membershipTier;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }
}
