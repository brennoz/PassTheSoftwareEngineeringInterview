package com.interview.ood.q056_card_authorization.solution;

import java.math.BigDecimal;

// Only implements the one abstract step (contactIssuerNetwork) — inherits validateCard,
// requiresManualReview (default: never), and logResult unchanged from the base class. That
// "only one method, nothing else" shape is the whole payoff of Template Method: this class is
// exactly as long as what's genuinely Visa-specific, and not one line longer.
// What NOT to do: don't override requiresManualReview() here just to explicitly return false
// "for clarity" — that duplicates the base class's default for no reason and creates a second
// place a future reader has to check to confirm Visa really has no review policy. Silence
// (not overriding the hook) IS the correct way to say "use the default."
public class VisaAuthorizationProcessor extends CardAuthorizationProcessor {

    private static final BigDecimal APPROVAL_LIMIT = new BigDecimal("5000");

    @Override
    protected AuthorizationResult contactIssuerNetwork(Card card, BigDecimal amount) {
        String lastFour = card.number().substring(card.number().length() - 4);
        if (amount.compareTo(APPROVAL_LIMIT) <= 0) {
            return AuthorizationResult.approved("V-" + lastFour);
        }
        return AuthorizationResult.declined("Visa: amount exceeds approval limit");
    }
}
