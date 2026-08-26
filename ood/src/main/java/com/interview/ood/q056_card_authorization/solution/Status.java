package com.interview.ood.q056_card_authorization.solution;

// Design decision: enum, not a String "status" field or a subclass-per-status hierarchy on
// AuthorizationResult. The three outcomes are a fixed, closed set — an enum gives type safety
// and lets a caller switch on it (no instanceof/type-switch needed, matching this question's
// constraint) without needing AuthorizationResult itself to be polymorphic.
// Rejected alternative: three separate result types (Approved/Declined/PendingReview) behind
// a sealed interface. Considered, since q050 covers exactly that shape — rejected here
// because none of the three outcomes carry genuinely different DATA (all three are just
// status + optional code + message), so a sealed hierarchy would add ceremony without buying
// anything a plain enum field doesn't already give.
public enum Status {
    APPROVED,
    DECLINED,
    PENDING_REVIEW
}
