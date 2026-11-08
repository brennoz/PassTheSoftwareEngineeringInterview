package com.interview.pairingdrill.q081_invoice_billing_service.solution;

import com.interview.pairingdrill.q081_invoice_billing_service.Invoice;
import com.interview.pairingdrill.q081_invoice_billing_service.InvoiceRepository;
import com.interview.pairingdrill.q081_invoice_billing_service.PaymentGatewayClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Solution tests — verify all 5 fixes work correctly.
 *
 * Test structure rationale:
 * - Each test covers one distinct behaviour path, not just one line of code.
 * - BigDecimal comparisons use compareTo()==0, not equals(), because
 *   BigDecimal.equals() is scale-sensitive (100.00 != 100.0).
 * - We use ArgumentCaptor on invoiceRepository.save() to verify WHAT was
 *   saved, not just that save() was called — this is the "loose mock" fix
 *   from the pairing-drill cross-cutting rules.
 */
@ExtendWith(MockitoExtension.class)
class SolutionTest {

    @Mock InvoiceRepository invoiceRepository;
    @Mock PaymentGatewayClient paymentGateway;

    InvoiceBillingService service;

    @BeforeEach
    void setUp() {
        service = new InvoiceBillingService(invoiceRepository, paymentGateway);
    }

    // --- createInvoice ---

    @Test
    void createInvoice_savesInvoiceWithDraftStatus() {
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.createInvoice("CUST-1", "PLAN-A", new BigDecimal("100.00"));

        ArgumentCaptor<Invoice> cap = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(cap.capture());
        // FIX 2: status stored as enum name, not arbitrary string
        assertEquals(InvoiceStatus.DRAFT.name(), cap.getValue().getStatus());
    }

    @Test
    void createInvoice_appliesDiscountCorrectly_usingBigDecimal() {
        // FIX 1: discount calculation must be exact — 10% of £99.99 = £9.999 → rounds to £10.00
        service.refreshRates(java.util.Map.of("PLAN-SENIOR", new BigDecimal("10")));
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.createInvoice("CUST-1", "PLAN-SENIOR", new BigDecimal("99.99"));

        ArgumentCaptor<Invoice> cap = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(cap.capture());
        // £99.99 - 10% (£10.00) = £89.99
        // compareTo not assertEquals — scale might differ (89.99 vs 89.990)
        assertEquals(0, new BigDecimal("89.99").compareTo(
            new BigDecimal(String.valueOf(cap.getValue().getTotalAmount()))));
    }

    // --- issueInvoice ---

    @Test
    void issueInvoice_transitionsStatusToIssued() {
        Invoice draft = invoiceWithStatus(InvoiceStatus.DRAFT);
        when(invoiceRepository.findById("INV-1")).thenReturn(Optional.of(draft));
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice result = service.issueInvoice("INV-1");

        // FIX 2: status is enum name
        assertEquals(InvoiceStatus.ISSUED.name(), result.getStatus());
        assertNotNull(result.getIssuedAt());
    }

    @Test
    void issueInvoice_throwsException_whenInvoiceAlreadyIssued() {
        Invoice issued = invoiceWithStatus(InvoiceStatus.ISSUED);
        when(invoiceRepository.findById("INV-2")).thenReturn(Optional.of(issued));

        // FIX 2: typed exception, not generic RuntimeException
        assertThrows(InvalidInvoiceStateException.class,
            () -> service.issueInvoice("INV-2"));
    }

    // --- applyPayment ---

    @Test
    void applyPayment_setsStatusToPaid_whenFullAmountReceived() throws Exception {
        Invoice issued = invoiceWithStatus(InvoiceStatus.ISSUED);
        issued.setTotalAmount(100.0);
        issued.setPaidAmount(0.0);
        when(invoiceRepository.findById("INV-3")).thenReturn(Optional.of(issued));
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice result = service.applyPayment("INV-3", new BigDecimal("100.00"));

        assertEquals(InvoiceStatus.PAID.name(), result.getStatus());
        verify(paymentGateway).charge(eq("INV-3"), anyDouble());
    }

    @Test
    void applyPayment_throwsPaymentGatewayException_whenGatewayFails() throws Exception {
        // FIX 5: gateway failure must NOT be swallowed — must propagate
        Invoice issued = invoiceWithStatus(InvoiceStatus.ISSUED);
        issued.setTotalAmount(100.0);
        issued.setPaidAmount(0.0);
        when(invoiceRepository.findById("INV-4")).thenReturn(Optional.of(issued));
        doThrow(new Exception("Gateway timeout"))
            .when(paymentGateway).charge(anyString(), anyDouble());

        assertThrows(PaymentGatewayException.class,
            () -> service.applyPayment("INV-4", new BigDecimal("50.00")));

        // Invoice must NOT be saved when gateway fails
        // (in real code @Transactional would roll back)
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void applyPayment_setsPartiallyPaid_whenPartialAmountReceived() throws Exception {
        Invoice issued = invoiceWithStatus(InvoiceStatus.ISSUED);
        issued.setTotalAmount(100.0);
        issued.setPaidAmount(0.0);
        when(invoiceRepository.findById("INV-5")).thenReturn(Optional.of(issued));
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice result = service.applyPayment("INV-5", new BigDecimal("40.00"));

        assertEquals(InvoiceStatus.PARTIALLY_PAID.name(), result.getStatus());
    }

    // Helper
    private Invoice invoiceWithStatus(InvoiceStatus status) {
        Invoice inv = new Invoice();
        inv.setId("INV-test");
        inv.setCustomerId("CUST-1");
        inv.setStatus(status.name());
        return inv;
    }
}
