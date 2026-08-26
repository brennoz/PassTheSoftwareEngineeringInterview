package com.interview.ood.q056_card_authorization.answer;

public record AuthorizationResult(Status status, String authorizationCode, String message) {

    public static AuthorizationResult pendingReview() {
        return new AuthorizationResult(Status.PENDING_REVIEW, "not authorized", "");
    }

    public static AuthorizationResult approved(String authorizationCode) {
        return new AuthorizationResult(Status.APPROVED, authorizationCode, "The card has been approved with code " + authorizationCode);
    }

    public static AuthorizationResult declined(String message) {
        return new AuthorizationResult(Status.DECLINED, "not authorized", message);
    }
}
