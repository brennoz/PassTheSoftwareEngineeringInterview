package com.interview.pairingdrill.q076_loan_disbursement;

import java.math.BigDecimal;

public class LedgerEntry {

    private Long loanAccountId;
    private String type;
    private BigDecimal amount;

    public LedgerEntry(Long loanAccountId, String type, BigDecimal amount) {
        this.loanAccountId = loanAccountId;
        this.type = type;
        this.amount = amount;
    }

    public Long getLoanAccountId() {
        return loanAccountId;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
