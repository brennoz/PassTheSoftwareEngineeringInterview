package com.interview.ood.q056_card_authorization.answer;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public abstract class CardAuthorizationProcessor {

    private List<String> logs = new ArrayList<>();

    public final AuthorizationResult authorize(Card card, BigDecimal amount) {

        validateCard(card);

        if (requiresManualReview(amount)) {
            return AuthorizationResult.pendingReview();
        }

        AuthorizationResult result = contactIssuerNetwork(card, amount);

        logResult(card, result);

        return result;
    }

    protected void validateCard(Card card) {
        if (card.yearMonth().isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("Card " + card.cardNumber() + " is expired");
        }
    }

    protected boolean requiresManualReview(BigDecimal amount) {
        return amount.compareTo(BigDecimal.valueOf(5000)) > 0;
    }

    protected abstract AuthorizationResult contactIssuerNetwork(Card card, BigDecimal amount);

    protected void logResult(Card card, AuthorizationResult result) {
        logs.add(card.ownerName() + result.message());
    }
}
