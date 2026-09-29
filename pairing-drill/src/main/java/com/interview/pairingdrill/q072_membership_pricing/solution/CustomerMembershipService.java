package com.interview.pairingdrill.q072_membership_pricing.solution;

public class CustomerMembershipService {

    private final CustomerRepository customerRepository;
    private final PaymentGateway paymentGateway;
    private final NotificationClient notificationClient;

    public CustomerMembershipService(CustomerRepository customerRepository,
                                      PaymentGateway paymentGateway,
                                      NotificationClient notificationClient) {
        this.customerRepository = customerRepository;
        this.paymentGateway = paymentGateway;
        this.notificationClient = notificationClient;
    }

    public void registerNewMembership(Customer customer, MembershipTier tier) {
        if (applyMembership(customer, tier)) {
            notificationClient.sendWelcomeEmail(customer.getBillingAccount().getContactEmail());
        }
    }

    public void renewMembership(Customer customer, MembershipTier tier) {
        if (applyMembership(customer, tier)) {
            notificationClient.sendRenewalEmail(customer.getBillingAccount().getContactEmail());
        }
    }

    // Why extracted: registerNewMembership and renewMembership were byte-for-byte identical
    // except for which notification fires afterward. Pulling the shared charge+persist step out
    // means the eventual idempotency-key fix (still open, see question.md) only needs to land
    // in one place.
    private boolean applyMembership(Customer customer, MembershipTier tier) {
        boolean charged = paymentGateway.charge(customer.getBillingAccount().getCardToken(), tier.getFee());
        if (charged) {
            customer.setMembershipTier(tier);
            customer.setMembershipStatus("ACTIVE");
            customerRepository.save(customer);
        }
        return charged;
    }

    public int getActiveMembershipCount() {
        return customerRepository.countByMembershipStatus("ACTIVE");
    }
}
