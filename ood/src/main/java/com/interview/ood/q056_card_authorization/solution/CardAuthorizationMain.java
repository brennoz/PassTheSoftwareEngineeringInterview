package com.interview.ood.q056_card_authorization.solution;

import java.math.BigDecimal;
import java.time.YearMonth;

// Runnable demo — not a test. Walks the same "same skeleton, different network" story a live
// interviewer would want talked through: one Card authorized through both processors, then
// Mastercard's stricter review hook actually short-circuiting the network call for a large
// amount — the audit log staying empty for that call is the observable proof.
public class CardAuthorizationMain {

    public static void main(String[] args) {
        Card card = new Card("4111111111111234", "Ada Lovelace", YearMonth.now().plusYears(2));

        System.out.println("=== Same card, same $1000 charge, two networks ===");
        CardAuthorizationProcessor visa = new VisaAuthorizationProcessor();
        CardAuthorizationProcessor mastercard = new MastercardAuthorizationProcessor();

        System.out.println("Visa:       " + visa.authorize(card, new BigDecimal("1000")));
        System.out.println("Mastercard: " + mastercard.authorize(card, new BigDecimal("1000")));

        System.out.println();
        System.out.println("=== Visa has no review policy: $8000 goes straight to decline via the network ===");
        System.out.println(visa.authorize(card, new BigDecimal("8000")));

        System.out.println();
        System.out.println("=== Mastercard's hook intercepts $8000 BEFORE the network is ever contacted ===");
        AuthorizationResult reviewResult = mastercard.authorize(card, new BigDecimal("8000"));
        System.out.println(reviewResult);
        System.out.println("Mastercard audit log so far: " + mastercard.getAuditLog());
        System.out.println("(only 1 entry — the $1000 call — proves the $8000 call never reached contactIssuerNetwork/logResult)");

        System.out.println();
        System.out.println("=== Expired card is rejected before any network is contacted, for either processor ===");
        Card expired = new Card("4111111111119999", "Charles Babbage", YearMonth.of(2000, 1));
        try {
            visa.authorize(expired, new BigDecimal("10"));
        } catch (IllegalArgumentException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }
    }
}
