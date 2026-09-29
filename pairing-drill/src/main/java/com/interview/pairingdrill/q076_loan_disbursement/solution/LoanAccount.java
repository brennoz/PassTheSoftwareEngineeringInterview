package com.interview.pairingdrill.q076_loan_disbursement.solution;

import java.math.BigDecimal;

public class LoanAccount {

    private Long id;
    private String status;
    private BigDecimal principal;
    // Why this exists: the concurrency-safe compare-and-set in
    // LoanAccountRepository.claimForProcessing needs something to compare. A real JPA entity
    // would use @Version and let Hibernate manage this automatically; modeled explicitly here
    // since this module deliberately doesn't run a real persistence context (project-wide
    // testing rule — unit tests + Mockito only, real infra verified manually).
    private long version;

    public LoanAccount() {
    }

    public LoanAccount(Long id, String status, BigDecimal principal, long version) {
        this.id = id;
        this.status = status;
        this.principal = principal;
        this.version = version;
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

    public long getVersion() {
        return version;
    }
}
