package com.interview.ood.q056_card_authorization.answer;

import java.math.BigDecimal;

public class VisaAuthorizationProcessor extends CardAuthorizationProcessor{

    @Override
    protected AuthorizationResult contactIssuerNetwork(Card card, BigDecimal amount) {
        if (!requiresManualReview(amount)) {
            String authorizationCode = "V-" + card.cardNumber().substring(card.cardNumber().length() - 4);
            return AuthorizationResult.approved(authorizationCode);
        }
        return AuthorizationResult.declined("Card " + card.cardNumber() + " has been declined");
    }
}
