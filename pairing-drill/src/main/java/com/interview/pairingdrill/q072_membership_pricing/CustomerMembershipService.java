package com.interview.pairingdrill.q072_membership_pricing;

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

    public void registerNewMembership(Customer customer, MembershipTier tier, String idempotencyKey) {
        if (applyMembership(customer, tier, idempotencyKey)) {
            notificationClient.sendWelcomeEmail(customer.getBillingAccount().getContactEmail());
        }
    }

    public void renewMembership(Customer customer, MembershipTier tier, String idempotencyKey) {
        if (applyMembership(customer, tier, idempotencyKey)) {
            notificationClient.sendRenewalEmail(customer.getBillingAccount().getContactEmail());
        }
    }

    private boolean applyMembership(Customer customer, MembershipTier tier, String idempotencyKey) {
        boolean charged = paymentGateway.charge(
                customer.getBillingAccount().getCardToken(), tier.getFee(), idempotencyKey);
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
