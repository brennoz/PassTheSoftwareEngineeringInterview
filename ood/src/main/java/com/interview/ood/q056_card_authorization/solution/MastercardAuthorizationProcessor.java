package com.interview.ood.q056_card_authorization.solution;

import java.math.BigDecimal;

// Overrides BOTH the hook (requiresManualReview) and the abstract step (contactIssuerNetwork)
// — demonstrating that a subclass may touch a hook when it genuinely needs to deviate, while
// VisaAuthorizationProcessor (which doesn't need a review policy) leaves the same hook alone.
// Because requiresManualReview() short-circuits authorize() before contactIssuerNetwork() is
// ever called (see the template method), REVIEW_THRESHOLD and APPROVAL_LIMIT below are two
// independent thresholds, not one — contactIssuerNetwork() only ever runs for amounts already
// below REVIEW_THRESHOLD, so its own decline path (amount > APPROVAL_LIMIT) only fires for
// the band between the two thresholds.
public class MastercardAuthorizationProcessor extends CardAuthorizationProcessor {

    private static final BigDecimal REVIEW_THRESHOLD = new BigDecimal("3000");
    private static final BigDecimal APPROVAL_LIMIT = new BigDecimal("2000");

    @Override
    protected boolean requiresManualReview(BigDecimal amount) {
        return amount.compareTo(REVIEW_THRESHOLD) > 0;
    }

    @Override
    protected AuthorizationResult contactIssuerNetwork(Card card, BigDecimal amount) {
        String lastFour = card.number().substring(card.number().length() - 4);
        if (amount.compareTo(APPROVAL_LIMIT) <= 0) {
            return AuthorizationResult.approved("MC-" + lastFour);
        }
        return AuthorizationResult.declined("Mastercard: amount exceeds approval limit");
    }
}
