package com.interview.pairingdrill.q078_payment_search.answer;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.LenientStubber;

@ExtendWith(MockitoExtension.class)
class MyAnswerTest {

    @Mock
    private PaymentRepository repository;

    private PaymentQueryService service;

    private Payment cardPending100;
    private Payment walletSettled200;
    private Payment cardPending50Flagged;
    private Payment walletPending300Flagged;

    @BeforeEach
    void setUp() {
        service = new PaymentQueryService(repository);

        cardPending100      = new Payment("p1", new BigDecimal("100.00"), PaymentStatus.PENDING,  PaymentType.CARD,   false);
        walletSettled200    = new Payment("p2", new BigDecimal("200.00"), PaymentStatus.SETTLED,  PaymentType.WALLET,  false);
        cardPending50Flagged= new Payment("p3", new BigDecimal("50.00"),  PaymentStatus.PENDING,  PaymentType.CARD,    true);
        walletPending300Flagged = new Payment("p4", new BigDecimal("300.00"), PaymentStatus.PENDING,  PaymentType.WALLET,  true);

        lenient().when(repository.findAll()).thenReturn(
            List.of(cardPending100, walletSettled200, cardPending50Flagged, walletPending300Flagged));
    }

    private LenientStubber lenient() {
        return Mockito.lenient();
    }

    @Test
    void search_filters_by_status_only() {

        SearchCriteria criteria = new SearchCriteria(null, null, PaymentStatus.PENDING, null);

        List<Payment> result = service.search(criteria);

        assertEquals(3, result.size());
        assertTrue(result.contains(cardPending100));
        assertTrue(result.contains(cardPending50Flagged));
        assertTrue(result.contains(walletPending300Flagged));
    }

    @Test
    void search_filters_by_amount_range_using_bigdecimal_precision() {
        SearchCriteria criteria = new SearchCriteria(
            new BigDecimal("60.00"), new BigDecimal("210.00"), null, null);

        List<Payment> result = service.search(criteria);

        assertEquals(2, result.size());
        assertTrue(result.contains(cardPending100));
        assertTrue(result.contains(walletSettled200));
    }

    @Test
    void search_with_empty_criteria_returns_all() {
        List<Payment> result = service.search(SearchCriteria.empty());

        assertEquals(4, result.size());
    }

    @Test
    void findForRisk_applies_type_filter_unlike_seeded_code() {

        SearchCriteria criteria = new SearchCriteria(null, null, null, PaymentType.CARD);

        List<Payment> result = service.findForRisk(criteria);

        assertEquals(1, result.size());
        assertTrue(result.contains(cardPending50Flagged));
        assertFalse(result.contains(walletPending300Flagged));
    }

    @Test
    void findForRisk_without_criteria_returns_only_flagged_payments() {
        
        List<Payment> result = service.findForRisk(SearchCriteria.empty());

        assertEquals(2, result.size());
        assertTrue(result.contains(cardPending50Flagged));
        assertTrue(result.contains(walletPending300Flagged));
        assertFalse(result.contains(cardPending100));
        assertFalse(result.contains(walletSettled200));
    }

    @Test
    void search_throws_when_criteria_is_null() {
        assertThrows(NullPointerException.class, () -> service.search(null));
    }
}
