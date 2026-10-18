package com.interview.pairingdrill.q078_payment_search.solution;

import java.math.BigDecimal;

// Why record not class: SearchCriteria is a pure value object — it carries filter
// parameters from the caller to the service and has no identity or behaviour.
// The seeded class had setters, meaning a caller could silently mutate criteria
// mid-flight (e.g. after passing it to a method). An immutable record prevents that
// class of bug and removes boilerplate equals/hashCode/toString.
//
// Why BigDecimal not double: the seeded class used double for minAmount/maxAmount.
// Comparing a BigDecimal payment amount to a double threshold via .doubleValue()
// introduces floating-point representation errors — 100.10 stored as a double is
// actually 100.09999999999... so a payment of exactly 100.10 could be incorrectly
// excluded. In a compliance context that is a regulatory liability.
// BigDecimal.compareTo() is exact.
//
// Why nullable BigDecimal fields rather than a sentinel (0.0 in the seeded code):
// the seeded code used `minAmount > 0` as "not set" — meaning a caller legitimately
// searching for payments of exactly £0.00 would get an incorrect result. null is the
// correct "not provided" signal.
public record SearchCriteria(BigDecimal minAmount, BigDecimal maxAmount, String status, String type) {

    // Factory for "no filters" — explicit intent; avoids passing four nulls at call sites.
    public static SearchCriteria empty() {
        return new SearchCriteria(null, null, null, null);
    }
}
