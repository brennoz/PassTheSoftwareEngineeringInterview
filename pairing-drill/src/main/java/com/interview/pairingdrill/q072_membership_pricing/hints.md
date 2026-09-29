# Hints — q072_membership_pricing

## Hint 1 — testing `activeMembershipCount`

Try it: from `MyAnswerTest` (same package, different class), can you actually read
`activeMembershipCount`? It's `private static int` — not `public`, not even package-private
(no modifier would've made it package-visible). A test in the same package still can't touch
`private` state without reflection.

That's not a gap in your testing knowledge — it's the code itself hiding state you can't
verify. Worth asking: if nothing outside the class can ever observe this field, what should
happen to it before you write a test for it?

## Solution — full

**Answering the `static → instance` question first, since it's the crux:** no, that wouldn't
fix the production issue. `CustomerMembershipService` is a `@Service` — Spring makes exactly
one instance and every request goes through that same object, so an instance field on a
singleton is exactly as shared across concurrent requests as a static one. Swapping to
instance-field-plus-`AtomicInteger` fixes thread-safety and, as a side effect, fixes the test
leakage — but it's polishing a design that shouldn't exist: nothing in this class ever *reads*
`activeMembershipCount`. It's write-only derived state duplicating something the database
already knows.

**Actual fix: delete the field, derive the count from the repository.**

```java
public enum MembershipTier {
    BASIC(new BigDecimal("250.00")),
    PREMIUM(new BigDecimal("750.00")),
    ENTERPRISE(new BigDecimal("2000.00"));

    private final BigDecimal fee;

    MembershipTier(BigDecimal fee) { this.fee = fee; }

    public BigDecimal getFee() { return fee; }
}
```

```java
public interface CustomerRepository {
    void save(Customer customer);
    int countByMembershipStatus(String status);
}
```

```java
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
```

`PaymentGateway.charge` also changes to take `BigDecimal`, not `double`. `Customer`'s
`membershipTier` field changes type from `String` to `MembershipTier`.

This single redesign kills, all at once: the thread-safety bug, the test-leakage bug that
actually failed in `mvn test`, the asymmetric-increment bug (register bumping the count, renew
not), the `double`-for-money violation, the `String` primitive-obsession/case-mismatch bug
class (same shape as rep 1's region-string bug), and the branch duplication between
`registerNewMembership`/`renewMembership` — replaced by one `MembershipTier` fee lookup and one
shared `applyMembership`.

**Tests change shape too** — no longer asserting a mutated counter, since counting isn't this
service's job anymore:

```java
@Test
void should_register_a_new_membership() {
    Customer customer = new Customer(1L, new BillingAccount("1234", "customer1@example.com"));
    Mockito.when(paymentGateway.charge(customer.getBillingAccount().getCardToken(), MembershipTier.PREMIUM.getFee()))
           .thenReturn(true);

    service.registerNewMembership(customer, MembershipTier.PREMIUM);

    assertEquals(MembershipTier.PREMIUM, customer.getMembershipTier());
    assertEquals("ACTIVE", customer.getMembershipStatus());
    Mockito.verify(notificationClient).sendWelcomeEmail("customer1@example.com");
}

@Test
void should_delegate_active_membership_count_to_repository() {
    Mockito.when(customerRepository.countByMembershipStatus("ACTIVE")).thenReturn(42);
    assertEquals(42, service.getActiveMembershipCount());
}
```

**The honest caveat — the actual graded skill here:** this is more ground than 45–60 minutes of
live pairing usually allows. A strong candidate wouldn't silently do all of this — they'd
sequence and narrate: fix the failing test first (smallest safe step — either the
`AtomicInteger` patch or going straight for the repository-query redesign if there's time),
state explicitly that `double → BigDecimal` and the `MembershipTier` enum are follow-ups if the
clock's tight, and leave the idempotency-key gap on `paymentGateway.charge` (no dedup key — a
retry would double-charge the customer; this project's own payment-domain rubric flags this by
name) explicitly parked rather than silently fixed or silently ignored.
