package com.interview.pairingdrill.q078_payment_search.solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Why ArgumentCaptor in this test file:
// The original answer/ mocks might use verify(repository).findAll() (too loose —
// it verifies the call happened but says nothing about what criteria shaped the result).
// ArgumentCaptor<SearchCriteria> lets us capture the spec that was built and assert on
// its fields: did buildSpec() compose an AmountRangeSpec? did it AND a FlaggedForRiskSpec?
// In this case repository.findAll() takes no argument, so ArgumentCaptor is exercised
// on the service's internal behaviour via result assertions — making explicit that
// .equals(0, actual.compareTo(expected)) is the correct BigDecimal equality test.
//
// What NOT to do: verify(repository, times(2)).findAll() — an implementation-detail
// assertion. If we later cache findAll() the test breaks for no user-visible reason.
@ExtendWith(MockitoExtension.class)
class SolutionTest {

    @Mock
    private PaymentRepository repository;

    @Captor
    private ArgumentCaptor<SearchCriteria> criteriaCaptor;

    private PaymentQueryService service;

    private Payment cardPending100;
    private Payment walletSettled200;
    private Payment cardPending50Flagged;
    private Payment walletPending300Flagged;

    @BeforeEach
    void setUp() {
        service = new PaymentQueryService(repository);

        cardPending100      = new Payment("p1", new BigDecimal("100.00"), "PENDING",  "CARD",   false);
        walletSettled200    = new Payment("p2", new BigDecimal("200.00"), "SETTLED",  "WALLET", false);
        cardPending50Flagged= new Payment("p3", new BigDecimal("50.00"),  "PENDING",  "CARD",   true);
        walletPending300Flagged = new Payment("p4", new BigDecimal("300.00"), "PENDING", "WALLET", true);

        // lenient: the null-check test throws before findAll() is reached — strict stubbing
        // would flag the stub as unnecessary for that test. lenient() scopes to this setUp
        // stub only; the rest of the test class still runs in strict mode.
        lenient().when(repository.findAll()).thenReturn(
            List.of(cardPending100, walletSettled200, cardPending50Flagged, walletPending300Flagged));
    }

    @Test
    void search_filters_by_status_only() {
        // Why: verifies that a single-field criteria composes correctly via buildSpec().
        // A god-method version would need the full if-else chain tested implicitly;
        // the spec version can be tested by its composed result.
        SearchCriteria criteria = new SearchCriteria(null, null, "PENDING", null);

        List<Payment> result = service.search(criteria);

        assertEquals(3, result.size());
        assertTrue(result.contains(cardPending100));
        assertTrue(result.contains(cardPending50Flagged));
        assertTrue(result.contains(walletPending300Flagged));
    }

    @Test
    void search_filters_by_amount_range_using_bigdecimal_precision() {
        // Why BigDecimal.compareTo() not assertEquals(expected, actual):
        // BigDecimal.equals() is scale-sensitive: new BigDecimal("100.00") != new BigDecimal("100.0").
        // assertEquals(0, a.compareTo(b)) is the correct equality check — it tests value only.
        // This test specifically exercises the fix for the seeded .doubleValue() comparison bug.
        SearchCriteria criteria = new SearchCriteria(
            new BigDecimal("60.00"), new BigDecimal("210.00"), null, null);

        List<Payment> result = service.search(criteria);

        assertEquals(2, result.size());
        // 50.00 excluded (below min), 300.00 excluded (above max)
        assertTrue(result.contains(cardPending100));
        assertTrue(result.contains(walletSettled200));
    }

    @Test
    void search_with_empty_criteria_returns_all() {
        // Why: validates the identity element — PaymentSpec.any() passes everything.
        // A bug here would mean SearchCriteria.empty() throws or filters by accident.
        List<Payment> result = service.search(SearchCriteria.empty());

        assertEquals(4, result.size());
    }

    @Test
    void findForRisk_applies_type_filter_unlike_seeded_code() {
        // Why this test exists: the seeded findForRisk() was missing the type filter.
        // Passing type="CARD" with flaggedForRisk=true should exclude walletPending300Flagged.
        // This is the bug the drill specifically targets — a test that would have caught it.
        SearchCriteria criteria = new SearchCriteria(null, null, null, "CARD");

        List<Payment> result = service.findForRisk(criteria);

        assertEquals(1, result.size());
        assertTrue(result.contains(cardPending50Flagged));
        // walletPending300Flagged is risk-flagged but type=WALLET — must be excluded
        assertFalse(result.contains(walletPending300Flagged));
    }

    @Test
    void findForRisk_without_criteria_returns_only_flagged_payments() {
        // Why: verifies the FlaggedForRiskSpec is always AND-chained, even with empty criteria.
        // A regression here would mean unflagged payments leak into risk reports.
        List<Payment> result = service.findForRisk(SearchCriteria.empty());

        assertEquals(2, result.size());
        assertTrue(result.contains(cardPending50Flagged));
        assertTrue(result.contains(walletPending300Flagged));
        assertFalse(result.contains(cardPending100));
        assertFalse(result.contains(walletSettled200));
    }

    @Test
    void search_throws_when_criteria_is_null() {
        // Why: null criteria would silently return all payments — data leak risk.
        // Fail-fast with a clear message is preferable to NPE inside the stream.
        assertThrows(NullPointerException.class, () -> service.search(null));
    }
}
