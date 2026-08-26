package com.interview.ood.q037_payment_gateway.solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

// TDD note: PaymentGateway is mocked, never a real acquirer call — these tests verify
// PaymentService's orchestration (state transitions, idempotency, repository updates), not
// gateway integration. That boundary is exactly what the PaymentGateway port exists for.
@ExtendWith(MockitoExtension.class)
class SolutionTest {

    @Mock
    private PaymentGateway gateway;

    private InMemoryPaymentRepository repository;
    private PaymentService service;
    private final Currency gbp = Currency.getInstance("GBP");

    @BeforeEach
    void setUp() {
        repository = new InMemoryPaymentRepository();
        service = new PaymentService(gateway, repository);
    }

    private PaymentRequest sampleRequest(String merchantReference) {
        return PaymentRequest.builder()
                .cardToken("tok_123")
                .amount(new Money(new BigDecimal("50.00"), gbp))
                .merchantId("merchant-1")
                .merchantReference(merchantReference)
                .build();
    }

    @Test
    void moneyAddSumsAmountsInTheSameCurrency() {
        Money ten = new Money(new BigDecimal("10.00"), gbp);
        Money five = new Money(new BigDecimal("5.00"), gbp);

        Money total = ten.add(five);

        assertEquals(0, total.amount().compareTo(new BigDecimal("15.00")));
    }

    @Test
    void moneySubtractThrowsWhenResultWouldGoNegative() {
        Money ten = new Money(new BigDecimal("10.00"), gbp);
        Money twenty = new Money(new BigDecimal("20.00"), gbp);

        assertThrows(IllegalArgumentException.class, () -> ten.subtract(twenty));
    }

    @Test
    void paymentRequestBuilderRejectsMissingRequiredField() {
        assertThrows(NullPointerException.class, () ->
                PaymentRequest.builder()
                        .cardToken("tok_123")
                        .merchantId("merchant-1")
                        .merchantReference("ref-missing-amount")
                        .build());
    }

    @Test
    void paymentStatusRejectsAnInvalidTransition() {
        // INITIATED can only reach AUTHORISED or FAILED — CAPTURED is not directly reachable,
        // skipping the authorisation step entirely.
        assertThrows(IllegalStateException.class, () ->
                PaymentStatus.INITIATED.assertCanTransitionTo(PaymentStatus.CAPTURED));
    }

    @Test
    void initiateIsIdempotentForTheSameMerchantReference() {
        PaymentRequest request = sampleRequest("ref-idempotent");

        Payment first = service.initiate(request);
        Payment second = service.initiate(request);

        // Same aggregate id back on retry — a client that resubmits after a timeout must not
        // get a second Payment created for the same merchantReference.
        assertEquals(first.id(), second.id());
    }

    @Test
    void authoriseSuccessMovesToAuthorisedAndRecordsGatewayTxId() {
        PaymentRequest request = sampleRequest("ref-auth-success");
        Payment payment = service.initiate(request);
        when(gateway.authorise(request)).thenReturn(new GatewayResponse("gw-tx-1", true, "ok"));

        Payment result = service.authorise(payment.id());

        assertEquals(PaymentStatus.AUTHORISED, result.status());
        assertEquals("gw-tx-1", result.gatewayTransactionId());
    }

    @Test
    void authoriseFailureMovesToFailed() {
        PaymentRequest request = sampleRequest("ref-auth-fail");
        Payment payment = service.initiate(request);
        when(gateway.authorise(request)).thenReturn(new GatewayResponse(null, false, "declined"));

        Payment result = service.authorise(payment.id());

        assertEquals(PaymentStatus.FAILED, result.status());
    }

    @Test
    void captureBeforeAuthorisationThrows() {
        PaymentRequest request = sampleRequest("ref-capture-too-early");
        Payment payment = service.initiate(request);

        assertThrows(IllegalStateException.class, () -> service.capture(payment.id()));
    }

    @Test
    void fullLifecycleAuthoriseCaptureRefundReachesRefunded() {
        PaymentRequest request = sampleRequest("ref-full-lifecycle");
        Payment payment = service.initiate(request);
        when(gateway.authorise(request)).thenReturn(new GatewayResponse("gw-tx-2", true, "ok"));
        service.authorise(payment.id());
        when(gateway.capture("gw-tx-2")).thenReturn(new GatewayResponse("gw-tx-2", true, "captured"));
        service.capture(payment.id());
        when(gateway.refund("gw-tx-2", request.amount())).thenReturn(new GatewayResponse("gw-tx-2", true, "refunded"));

        Payment result = service.refund(payment.id(), request.amount());

        assertEquals(PaymentStatus.REFUNDED, result.status());
    }

    @Test
    void findOrThrowThrowsForAnUnknownPaymentId() {
        assertThrows(IllegalArgumentException.class, () -> service.findOrThrow("does-not-exist"));
    }
}
