package com.interview.ood.q056_card_authorization.solution;

import java.time.YearMonth;

// Design decision: thin data record, no behaviour — matches this project's prod-leaning OOD
// convention (see q033 onward): the domain object holds state, the service (here,
// CardAuthorizationProcessor) owns the logic that acts on it, including validating it.
// Rejected alternative: a Card.isExpired() method living on the record itself. Rejected
// because "expired relative to what clock" is a policy question the record shouldn't need to
// know — CardAuthorizationProcessor.validateCard() is the one place that decision is made,
// and keeping it there means a future change (e.g. injecting a fixed Clock for testability)
// only touches one class, not the record every caller depends on.
// What NOT to do: don't add a mutable "status" field to Card to track whether it's been
// validated — validation is a one-shot check performed fresh on every authorize() call, not
// state that lives on the card itself.
public record Card(String number, String cardholderName, YearMonth expiry) {}
