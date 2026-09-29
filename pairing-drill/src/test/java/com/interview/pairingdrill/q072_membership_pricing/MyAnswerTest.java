package com.interview.pairingdrill.q072_membership_pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MyAnswerTest {

    private final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
    private final PaymentGateway paymentGateway = Mockito.mock(PaymentGateway.class);
    private final NotificationClient notificationClient = Mockito.mock(NotificationClient.class);

    private final CustomerMembershipService service =
            new CustomerMembershipService(customerRepository, paymentGateway, notificationClient);

    @Test
    void should_register_a_new_membership() {
        Customer customer = new Customer(1L, new BillingAccount("1234", "customer1@example.com"));
        Mockito.when(paymentGateway.charge(
                        customer.getBillingAccount().getCardToken(), MembershipTier.PREMIUM.getFee(), "key-1"))
               .thenReturn(true);

        service.registerNewMembership(customer, MembershipTier.PREMIUM, "key-1");

        assertEquals(MembershipTier.PREMIUM, customer.getMembershipTier());
        assertEquals("ACTIVE", customer.getMembershipStatus());
        Mockito.verify(customerRepository).save(customer);
        Mockito.verify(notificationClient).sendWelcomeEmail("customer1@example.com");
    }

    @Test
    void should_renew_membership() {
        Customer customer = new Customer(1L, new BillingAccount("1234", "customer1@example.com"));
        Mockito.when(paymentGateway.charge(
                        customer.getBillingAccount().getCardToken(), MembershipTier.PREMIUM.getFee(), "key-2"))
               .thenReturn(true);

        service.renewMembership(customer, MembershipTier.PREMIUM, "key-2");

        assertEquals(MembershipTier.PREMIUM, customer.getMembershipTier());
        assertEquals("ACTIVE", customer.getMembershipStatus());
        Mockito.verify(notificationClient).sendRenewalEmail("customer1@example.com");
        Mockito.verify(notificationClient, Mockito.never()).sendWelcomeEmail(Mockito.anyString());
    }

    @Test
    void should_not_activate_membership_when_charge_fails() {
        Customer customer = new Customer(1L, new BillingAccount("1234", "customer1@example.com"));
        Mockito.when(paymentGateway.charge(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
               .thenReturn(false);

        service.registerNewMembership(customer, MembershipTier.BASIC, "key-3");

        assertNull(customer.getMembershipTier());
        assertNull(customer.getMembershipStatus());
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(notificationClient, Mockito.never()).sendWelcomeEmail(Mockito.anyString());
    }

    @Test
    void should_delegate_active_membership_count_to_repository() {
        Mockito.when(customerRepository.countByMembershipStatus("ACTIVE")).thenReturn(42);
        assertEquals(42, service.getActiveMembershipCount());
    }
}
