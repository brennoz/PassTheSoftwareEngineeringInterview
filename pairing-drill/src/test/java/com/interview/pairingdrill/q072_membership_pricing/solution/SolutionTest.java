package com.interview.pairingdrill.q072_membership_pricing.solution;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SolutionTest {

    private final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
    private final PaymentGateway paymentGateway = Mockito.mock(PaymentGateway.class);
    private final NotificationClient notificationClient = Mockito.mock(NotificationClient.class);

    private final CustomerMembershipService service =
            new CustomerMembershipService(customerRepository, paymentGateway, notificationClient);

    @Test
    void registersNewMembershipAndSendsWelcomeEmail() {
        Customer customer = new Customer(1L, new BillingAccount("1234", "customer1@example.com"));
        Mockito.when(paymentGateway.charge(customer.getBillingAccount().getCardToken(), MembershipTier.PREMIUM.getFee()))
               .thenReturn(true);

        service.registerNewMembership(customer, MembershipTier.PREMIUM);

        assertEquals(MembershipTier.PREMIUM, customer.getMembershipTier());
        assertEquals("ACTIVE", customer.getMembershipStatus());
        Mockito.verify(customerRepository).save(customer);
        Mockito.verify(notificationClient).sendWelcomeEmail("customer1@example.com");
        Mockito.verify(notificationClient, Mockito.never()).sendRenewalEmail(Mockito.anyString());
    }

    @Test
    void renewsMembershipAndSendsRenewalEmail() {
        Customer customer = new Customer(1L, new BillingAccount("1234", "customer1@example.com"));
        Mockito.when(paymentGateway.charge(customer.getBillingAccount().getCardToken(), MembershipTier.PREMIUM.getFee()))
               .thenReturn(true);

        service.renewMembership(customer, MembershipTier.PREMIUM);

        assertEquals(MembershipTier.PREMIUM, customer.getMembershipTier());
        assertEquals("ACTIVE", customer.getMembershipStatus());
        Mockito.verify(notificationClient).sendRenewalEmail("customer1@example.com");
        Mockito.verify(notificationClient, Mockito.never()).sendWelcomeEmail(Mockito.anyString());
    }

    @Test
    void doesNotActivateMembershipWhenChargeFails() {
        Customer customer = new Customer(1L, new BillingAccount("1234", "customer1@example.com"));
        Mockito.when(paymentGateway.charge(Mockito.anyString(), Mockito.any())).thenReturn(false);

        service.registerNewMembership(customer, MembershipTier.BASIC);

        assertEquals(null, customer.getMembershipTier());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(notificationClient, Mockito.never()).sendWelcomeEmail(Mockito.anyString());
    }

    // Why this replaces asserting a mutated counter: getActiveMembershipCount no longer holds
    // any state of its own, so the only thing worth testing is that it delegates correctly —
    // asserting behaviour, not implementation, of a pure pass-through.
    @Test
    void activeMembershipCountDelegatesToRepository() {
        Mockito.when(customerRepository.countByMembershipStatus("ACTIVE")).thenReturn(42);

        assertEquals(42, service.getActiveMembershipCount());
    }
}
