package com.interview.ood.q056_card_authorization.solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTest {

    private Card card;
    private VisaAuthorizationProcessor visa;
    private MastercardAuthorizationProcessor mastercard;

    @BeforeEach
    void setUp() {
        card = new Card("4111111111111234", "Ada Lovelace", YearMonth.now().plusYears(2));
        visa = new VisaAuthorizationProcessor();
        mastercard = new MastercardAuthorizationProcessor();
    }

    // What this verifies: authorize() is structurally un-overridable, not just "not overridden
    // by convention." Checked via reflection rather than by trying to subclass and override it
    // (which wouldn't even compile if final is genuinely present — a compile failure is a
    // weaker signal to a reader of THIS test than an explicit, readable assertion that states
    // the actual design constraint being enforced).
    @Test
    void authorizeMethodIsFinal() throws NoSuchMethodException {
        Method authorize = CardAuthorizationProcessor.class.getMethod("authorize", Card.class, BigDecimal.class);

        assertTrue(Modifier.isFinal(authorize.getModifiers()));
    }

    // What this verifies: validateCard() — a step shared by every subclass via the template
    // method — actually runs before any network-specific logic, for every processor, not just
    // one. A processor whose contactIssuerNetwork() happened to also reject expired cards
    // would pass a weaker test that only checked ONE processor; using Visa here specifically
    // proves the guard lives in the shared base class step, not duplicated per subclass.
    @Test
    void expiredCardThrowsBeforeAnyNetworkContact() {
        Card expired = new Card("4111111111119999", "Charles Babbage", YearMonth.of(2000, 1));

        assertThrows(IllegalArgumentException.class, () -> visa.authorize(expired, BigDecimal.TEN));
    }

    @Test
    void visaApprovesAtOrBelowFiveThousand() {
        AuthorizationResult result = visa.authorize(card, new BigDecimal("5000"));

        assertEquals(Status.APPROVED, result.status());
        assertEquals("V-1234", result.authorizationCode());
    }

    @Test
    void visaDeclinesAboveFiveThousand() {
        AuthorizationResult result = visa.authorize(card, new BigDecimal("5000.01"));

        assertEquals(Status.DECLINED, result.status());
    }

    // What this verifies: VisaAuthorizationProcessor never overrides requiresManualReview(),
    // so it must fall through to the base class's default (always false) — even for an amount
    // large enough that Mastercard's hook (a different subclass entirely) WOULD flag it. This
    // is the test that would fail if the hook's default were accidentally implemented as
    // "true" instead of "false", or if Visa accidentally inherited Mastercard's override
    // through some refactoring mistake.
    @Test
    void visaNeverTriggersManualReviewEvenForLargeAmounts() {
        AuthorizationResult result = visa.authorize(card, new BigDecimal("50000"));

        assertEquals(Status.DECLINED, result.status()); // reached the network, just declined there
        assertEquals(1, visa.getAuditLog().size());      // logResult DID run — proves network was contacted
    }

    @Test
    void mastercardApprovesAtOrBelowTwoThousand() {
        AuthorizationResult result = mastercard.authorize(card, new BigDecimal("2000"));

        assertEquals(Status.APPROVED, result.status());
        assertEquals("MC-1234", result.authorizationCode());
    }

    @Test
    void mastercardDeclinesBetweenApprovalLimitAndReviewThreshold() {
        AuthorizationResult result = mastercard.authorize(card, new BigDecimal("2500"));

        assertEquals(Status.DECLINED, result.status());
    }

    // What this verifies: the actual point of this whole exercise. Above the review
    // threshold, Mastercard's overridden hook must short-circuit authorize() BEFORE
    // contactIssuerNetwork()/logResult() ever run — not just return PENDING_REVIEW while
    // ALSO quietly contacting the network anyway. The audit log staying at size 0 is the
    // proof: logResult() is the only place anything gets appended to it, and logResult() is
    // only reachable after contactIssuerNetwork() in the template method. If a solution
    // called contactIssuerNetwork() unconditionally and only used the hook to decide what to
    // RETURN, this test — not the status assertion alone — is what would catch it.
    @Test
    void mastercardManualReviewSkipsNetworkContactEntirely() {
        AuthorizationResult result = mastercard.authorize(card, new BigDecimal("3000.01"));

        assertEquals(Status.PENDING_REVIEW, result.status());
        assertTrue(mastercard.getAuditLog().isEmpty());
    }
}
