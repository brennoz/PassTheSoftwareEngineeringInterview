package com.interview.ood.q056_card_authorization.solution;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

// Pattern: Template Method — the fixed sequence (validate, check-for-review, contact
// network, log) is identical for every card network; only "how do you actually talk to the
// network" genuinely differs per subtype. Putting that shared sequence here, once, means a
// future card network (Amex, a debit rail, whatever) gets validation, the review hook, and
// logging for free just by extending this class and implementing one method.
//
// Rejected alternative: Strategy (q043's SchedulingStrategy shape) — inject a
// NetworkGateway strategy object into a single concrete CardAuthorizationService instead of
// subclassing. Rejected specifically because the thing that varies here is ONE STEP inside an
// otherwise-fixed sequence, not the whole algorithm — Strategy would still need somewhere to
// put the shared validate/review/log logic, and that "somewhere" would just be this same
// class again, now holding a strategy reference instead of being extended. Template Method is
// the more direct fit when most of the algorithm is shared and only a step or two differs;
// see topics/ood/template-method/0001-*.html for the fuller comparison.
//
// What NOT to do: don't make authorize() non-final "to allow flexibility." The entire point
// of this pattern is that the SEQUENCE is guaranteed — a non-final template method lets a
// future subclass silently skip validateCard() or reorder steps, which is exactly the kind of
// bug this design exists to make structurally impossible, not just discouraged by convention.
public abstract class CardAuthorizationProcessor {

    private final List<String> auditLog = new ArrayList<>();

    // The template method. Final — see class-level comment for why. Notice this method
    // contains ZERO network-specific logic — every line here is either a call to a shared
    // concrete step (validateCard, logResult) or a call to a step this class explicitly
    // delegates to subtypes (requiresManualReview as a hook, contactIssuerNetwork as the
    // mandatory abstract step).
    public final AuthorizationResult authorize(Card card, BigDecimal amount) {
        validateCard(card);

        if (requiresManualReview(amount)) {
            // What NOT to do: don't call contactIssuerNetwork() here "just to log what would
            // have happened" — the whole point of the manual-review hook is to SKIP the
            // network call entirely (e.g. to avoid an unnecessary, possibly costly, network
            // round-trip for a transaction that's going to a human reviewer regardless of
            // what the network would say). A test in this suite specifically checks the
            // network step is never reached when this branch is taken.
            return AuthorizationResult.pendingReview();
        }

        AuthorizationResult result = contactIssuerNetwork(card, amount);
        logResult(card, result);
        return result;
    }

    // Concrete step, shared by every subclass. Deliberately not comparing against a Clock the
    // caller can inject — this project's YAGNI convention: nothing in this exercise's scope
    // needs to simulate "a different day," so a real YearMonth.now() call is the simplest
    // correct implementation, not a premature testability abstraction.
    protected void validateCard(Card card) {
        if (card.expiry().isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("Card expired: " + card.number());
        }
    }

    // Hook: has a sensible default (never require manual review) rather than being abstract.
    // Most networks don't need this — VisaAuthorizationProcessor never overrides it. Only a
    // subclass with an actual stricter-review policy (MastercardAuthorizationProcessor here)
    // needs to touch it. Contrast with contactIssuerNetwork() below, which has NO default
    // because there is no sensible universal way to "contact a network" in general.
    protected boolean requiresManualReview(BigDecimal amount) {
        return false;
    }

    // Abstract primitive operation — the one step this class cannot supply a default for.
    // What NOT to do: don't give this a default implementation that throws
    // UnsupportedOperationException "to save typing abstract." That would compile, but it
    // moves a mistake (a subclass that forgot to implement network contact) from a compile
    // error to a runtime surprise the first time that subclass is actually used — exactly the
    // failure mode `abstract` exists to prevent.
    protected abstract AuthorizationResult contactIssuerNetwork(Card card, BigDecimal amount);

    // Concrete step, shared. A real implementation would ship this to a logging framework or
    // an audit table; an in-memory list is enough to prove the step runs and runs with the
    // right data, which is what this exercise's tests actually check.
    protected void logResult(Card card, AuthorizationResult result) {
        auditLog.add(card.number() + " -> " + result.status());
    }

    public List<String> getAuditLog() {
        return List.copyOf(auditLog);
    }
}
