package com.interview.pairingdrill.q076_loan_disbursement;

import java.math.BigDecimal;

public class LoanAccount {

    private Long id;
    private String status;
    private BigDecimal principal;

    public LoanAccount() {
    }

    public LoanAccount(Long id, String status, BigDecimal principal) {
        this.id = id;
        this.status = status;
        this.principal = principal;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }
}
