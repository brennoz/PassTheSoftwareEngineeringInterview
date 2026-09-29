package com.interview.prep.bankco.ex2_transaction_functional_toolkit.solution;

public class ValidationResult {

    private final boolean approved;
    private final String reason;

    private ValidationResult(boolean approved, String reason) {
        this.approved = approved;
        this.reason = reason;
    }

    public static ValidationResult approved() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult rejected(String reason) {
        return new ValidationResult(false, reason);
    }

    public boolean isApproved() {
        return approved;
    }

    public String getReason() {
        return reason;
    }
}
