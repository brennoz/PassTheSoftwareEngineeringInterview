package com.interview.spring.q128_invoice_payment_service.solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Unit tests: InvoiceService only, InvoiceRepository mocked.
// No @SpringBootTest — no Spring context, no H2, no port. Fast, isolated.
// This is the correct level for service unit tests: verify business rules, not infrastructure.
//
// Why mock the repository and not use H2?
// H2 tests verify that SQL queries work — useful, but that's InvoiceRepository's contract
// with the DB, not InvoiceService's contract with the domain. These tests would catch
// InvoiceService bugs even if the DB schema changed tomorrow.
// For H2 integration tests, use @DataJpaTest in a separate test class.
class SolutionTest {

    private static final LocalDate DUE_DATE = LocalDate.of(2027, 1, 31);

    private InvoiceRepository repository;
    private InvoiceService service;

    @BeforeEach
    void setUp() {
        // Hand-build the service with a Mockito mock — no Spring context needed.
        // This is only possible because InvoiceService uses constructor injection.
        // Field-injected services cannot be instantiated this way without Mockito's
        // @InjectMocks + @ExtendWith(MockitoExtension.class) ceremony.
        repository = mock(InvoiceRepository.class);
        service    = new InvoiceService(repository);
    }

    @Test
    void createInvoice_returns_draft_invoice_and_saves_it() {
        // returnAnswer: returns the exact Invoice passed to save() — simulates JPA save()
        // returning the persisted entity. Without this, save() returns null and getStatus() NPEs.
        when(repository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        Invoice result = service.createInvoice("CUST-1", DUE_DATE);

        assertEquals(InvoiceStatus.DRAFT, result.getStatus());
        assertEquals("CUST-1", result.getCustomerId());
        assertNotNull(result.getId());
        verify(repository).save(any(Invoice.class));
    }

    @Test
    void issueInvoice_transitions_status_to_issued() {
        Invoice draft = Invoice.create("CUST-2", DUE_DATE);
        when(repository.findById(draft.getId())).thenReturn(Optional.of(draft));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice result = service.issueInvoice(draft.getId());

        assertEquals(InvoiceStatus.ISSUED, result.getStatus());
        assertNotNull(result.getIssueDate());
    }

    @Test
    void issueInvoice_throws_when_invoice_already_issued() {
        // Why test this here rather than in InvoiceStatus or Invoice directly?
        // This test verifies that InvoiceService correctly propagates domain exceptions —
        // that the service doesn't swallow them or wrap them in something else.
        Invoice issued = Invoice.create("CUST-3", DUE_DATE);
        issued.issue();
        when(repository.findById(issued.getId())).thenReturn(Optional.of(issued));

        assertThrows(InvalidStateTransitionException.class,
            () -> service.issueInvoice(issued.getId()));
        // save must not be called — exception fires before the save
        verify(repository, never()).save(any());
    }

    @Test
    void recordPayment_transitions_to_paid_when_full_amount() {
        Invoice invoice = buildIssuedInvoice("CUST-4", new BigDecimal("200.00"));
        when(repository.findById(invoice.getId())).thenReturn(Optional.of(invoice));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice result = service.recordPayment(invoice.getId(), new BigDecimal("200.00"));

        assertEquals(InvoiceStatus.PAID, result.getStatus());
        // amountDue() must be exactly 0 after full payment — compareTo, not equals
        assertEquals(0, result.amountDue().compareTo(BigDecimal.ZERO));
    }

    @Test
    void recordPayment_transitions_to_partially_paid_for_partial_amount() {
        Invoice invoice = buildIssuedInvoice("CUST-5", new BigDecimal("200.00"));
        when(repository.findById(invoice.getId())).thenReturn(Optional.of(invoice));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice result = service.recordPayment(invoice.getId(), new BigDecimal("80.00"));

        assertEquals(InvoiceStatus.PARTIALLY_PAID, result.getStatus());
        assertEquals(0, result.amountDue().compareTo(new BigDecimal("120.00")));
    }

    @Test
    void recordPayment_throws_on_overpayment() {
        Invoice invoice = buildIssuedInvoice("CUST-6", new BigDecimal("100.00"));
        when(repository.findById(invoice.getId())).thenReturn(Optional.of(invoice));

        assertThrows(IllegalArgumentException.class,
            () -> service.recordPayment(invoice.getId(), new BigDecimal("150.00")));
        verify(repository, never()).save(any());
    }

    @Test
    void getInvoice_throws_not_found_when_id_unknown() {
        when(repository.findById("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class,
            () -> service.getInvoice("UNKNOWN"));
    }

    // Helper: creates an invoice with one line item and issues it.
    // Built in the test class rather than as test fixtures to keep each test's
    // setup visible and avoid shared mutable state between tests.
    private Invoice buildIssuedInvoice(String customerId, BigDecimal totalAmount) {
        Invoice invoice = Invoice.create(customerId, DUE_DATE);
        invoice.addLineItem("Service charge", totalAmount, 1);
        invoice.issue();
        return invoice;
    }
}
