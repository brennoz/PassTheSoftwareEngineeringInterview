package com.interview.pairingdrill.q076_loan_disbursement.solution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SolutionTest {

    private final LoanAccountRepository loanAccountRepository = Mockito.mock(LoanAccountRepository.class);
    private final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
    private final CreditBureauClient creditBureauClient = Mockito.mock(CreditBureauClient.class);
    private final LoanDisbursementService service =
            new LoanDisbursementService(loanAccountRepository, ledgerRepository, creditBureauClient);

    @Test
    void disbursesLoanWhenCreditScoreIsSufficient() {
        LoanAccount account = new LoanAccount(1L, "PENDING", new BigDecimal("5000"), 0L);
        Mockito.when(loanAccountRepository.findById(1L)).thenReturn(account);
        Mockito.when(loanAccountRepository.claimForProcessing(1L, 0L)).thenReturn(true);
        Mockito.when(creditBureauClient.checkCreditScore("cust-1")).thenReturn(700);

        service.disburseLoan(1L, "cust-1");

        assertEquals("DISBURSED", account.getStatus());
        Mockito.verify(ledgerRepository).append(Mockito.argThat(
                entry -> entry.getLoanAccountId().equals(1L)
                        && entry.getType().equals("DISBURSEMENT")
                        && entry.getAmount().compareTo(new BigDecimal("5000")) == 0));
        Mockito.verify(loanAccountRepository, Mockito.times(2)).save(account); // APPROVED, then DISBURSED
    }

    @Test
    void rejectsLoanWhenCreditScoreIsInsufficient() {
        LoanAccount account = new LoanAccount(1L, "PENDING", new BigDecimal("5000"), 0L);
        Mockito.when(loanAccountRepository.findById(1L)).thenReturn(account);
        Mockito.when(loanAccountRepository.claimForProcessing(1L, 0L)).thenReturn(true);
        Mockito.when(creditBureauClient.checkCreditScore("cust-1")).thenReturn(400);

        service.disburseLoan(1L, "cust-1");

        assertEquals("REJECTED", account.getStatus());
        Mockito.verify(ledgerRepository, Mockito.never()).append(Mockito.any());
    }

    // This is the regression test for the actual reported incident: two concurrent calls
    // reading the same PENDING account must not both succeed. The second caller's
    // claimForProcessing returns false — simulating what a real DB's optimistic-lock
    // compare-and-set does when a concurrent writer already moved the row on.
    @Test
    void refusesDuplicateDisbursementWhenClaimFails() {
        LoanAccount account = new LoanAccount(1L, "PENDING", new BigDecimal("5000"), 0L);
        Mockito.when(loanAccountRepository.findById(1L)).thenReturn(account);
        Mockito.when(loanAccountRepository.claimForProcessing(1L, 0L)).thenReturn(false);

        assertThrows(DuplicateDisbursementException.class, () -> service.disburseLoan(1L, "cust-1"));

        Mockito.verify(creditBureauClient, Mockito.never()).checkCreditScore(Mockito.anyString());
        Mockito.verify(ledgerRepository, Mockito.never()).append(Mockito.any());
    }

    @Test
    void wrapsASlowCreditBureauInATimeout() {
        LoanAccount account = new LoanAccount(1L, "PENDING", new BigDecimal("5000"), 0L);
        Mockito.when(loanAccountRepository.findById(1L)).thenReturn(account);
        Mockito.when(loanAccountRepository.claimForProcessing(1L, 0L)).thenReturn(true);
        Mockito.when(creditBureauClient.checkCreditScore("cust-1")).thenAnswer(invocation -> {
            Thread.sleep(4000); // longer than CREDIT_CHECK_TIMEOUT (3s)
            return 700;
        });

        assertThrows(CreditCheckTimeoutException.class, () -> service.disburseLoan(1L, "cust-1"));
    }
}
