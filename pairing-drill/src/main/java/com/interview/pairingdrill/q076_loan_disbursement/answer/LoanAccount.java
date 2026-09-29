package com.interview.pairingdrill.q076_loan_disbursement.answer;

import java.math.BigDecimal;

public class LoanAccount {

    private Long id;
    private LoanStatus status;
    private BigDecimal principal;
    private long version;

    public LoanAccount() {
    }

    public LoanAccount(Long id, LoanStatus status, BigDecimal principal, long version) {
        this.id = id;
        this.status = status;
        this.principal = principal;
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public long getVersion() {
        return version;
    }    
}
