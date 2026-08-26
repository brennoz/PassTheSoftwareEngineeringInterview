package com.interview.ood.q056_card_authorization.solution;

// Design decision: one flat record with three named factory methods, rather than exposing the
// canonical constructor directly at every call site. approved()/declined()/pendingReview()
// each pin down which fields make sense together (e.g. a declined result has no
// authorizationCode) so a caller can't accidentally construct a nonsensical combination like
// new AuthorizationResult(DECLINED, "V-1234", null) — the factory methods are the only
// sanctioned way to build one, and each one only takes the fields that combination actually
// needs.
// What NOT to do: don't add a fourth "REJECTED" or "ERROR" status "just in case" without a
// concrete step in the template method that would ever produce it — this project's YAGNI
// convention applies to enum values too, not just classes.
public record AuthorizationResult(Status status, String authorizationCode, String message) {

    public static AuthorizationResult approved(String authorizationCode) {
        return new AuthorizationResult(Status.APPROVED, authorizationCode, "Approved");
    }

    public static AuthorizationResult declined(String message) {
        return new AuthorizationResult(Status.DECLINED, null, message);
    }

    public static AuthorizationResult pendingReview() {
        return new AuthorizationResult(Status.PENDING_REVIEW, null, "Flagged for manual review");
    }
}
