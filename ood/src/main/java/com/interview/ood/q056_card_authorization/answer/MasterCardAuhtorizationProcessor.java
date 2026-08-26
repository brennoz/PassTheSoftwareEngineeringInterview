package com.interview.ood.q056_card_authorization.answer;

import java.math.BigDecimal;

public class MasterCardAuhtorizationProcessor extends CardAuthorizationProcessor {

    @Override
    protected AuthorizationResult contactIssuerNetwork(Card card, BigDecimal amount) {
        if (requiresManualReview(amount)) {
            return AuthorizationResult.declined("Card " + card.cardNumber() + " has been declined");    
        }
        if (amount.compareTo(BigDecimal.valueOf(2000)) > 0) {
            String authorizationCode = "C-" + card.cardNumber().substring(card.cardNumber().length() - 4);
            return AuthorizationResult.approved(authorizationCode);
        }
        return AuthorizationResult.declined("Card " + card.cardNumber() + " has been declined");
    }

    @Override
    protected boolean requiresManualReview(BigDecimal amount) {
        return !(amount.compareTo(BigDecimal.valueOf(3000)) <= 0);
    }
}
