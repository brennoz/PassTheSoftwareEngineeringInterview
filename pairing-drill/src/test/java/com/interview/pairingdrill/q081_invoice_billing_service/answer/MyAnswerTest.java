package com.interview.pairingdrill.q081_invoice_billing_service.answer;

import com.interview.pairingdrill.q081_invoice_billing_service.Invoice;
import com.interview.pairingdrill.q081_invoice_billing_service.InvoiceRepository;
import com.interview.pairingdrill.q081_invoice_billing_service.PaymentGatewayClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Write your tests here as you refactor answer/InvoiceBillingService.java.
 *
 * Mocks are pre-wired. Add @Test methods.
 * Use JUnit 5 (Assertions.*) and Mockito only — no AssertJ.
 * Use ArgumentCaptor to verify what was saved to the repository.
 * Use compareTo()==0 for BigDecimal comparisons.
 */
@ExtendWith(MockitoExtension.class)
class MyAnswerTest {

    @Mock InvoiceRepository invoiceRepository;
    @Mock PaymentGatewayClient paymentGateway;

    // TODO: instantiate your refactored InvoiceBillingService here

    @BeforeEach
    void setUp() {
        // TODO: service = new InvoiceBillingService(invoiceRepository, paymentGateway);
    }

    // TODO: write tests
}
