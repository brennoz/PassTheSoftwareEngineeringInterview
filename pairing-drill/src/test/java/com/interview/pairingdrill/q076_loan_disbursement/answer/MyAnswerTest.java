package com.interview.pairingdrill.q076_loan_disbursement.answer;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MyAnswerTest {

    private final LoanAccountRepository loanAccountRepository = Mockito.mock(LoanAccountRepository.class);
    private final LedgerRepository ledgerRepository = Mockito.mock(LedgerRepository.class);
    private final CreditBureauClient creditBureauClient = Mockito.mock(CreditBureauClient.class);

    private final LoanDisbursementService service = new LoanDisbursementService(loanAccountRepository, ledgerRepository, creditBureauClient);

    // Write your tests here.
    @Test 
    void should_disburse_loan_when_credit_score_is_sufficient() {
        LoanAccount account = new LoanAccount(1L, LoanStatus.PENDING, BigDecimal.valueOf(5000), 0);
        Mockito.when(loanAccountRepository.findById(1L)).thenReturn(account);
        Mockito.when(loanAccountRepository.checkBeforeProcessing(1L, 0L)).thenReturn(true);
        Mockito.when(creditBureauClient.checkCreditScore("cust-1")).thenReturn(700);

        service.disburseLoan(1L, "cust-1");

        assertEquals(LoanStatus.DISBURSED, account.getStatus());
        Mockito.verify(ledgerRepository).append(Mockito.argThat(
            entry -> entry.getLoanAccountId().equals(1L) 
                    && entry.getType().equals("DISBURSEMENT") 
                    && entry.getAmount().compareTo(BigDecimal.valueOf(5000)) == 0)
        );
        Mockito.verify(loanAccountRepository, Mockito.times(2)).save(account);
    }

    @Test
    void rejectsLoanWhenCreditScoreIsInsufficient() {
        LoanAccount account = new LoanAccount(1L, LoanStatus.PENDING, new BigDecimal("5000"), 1L);
        Mockito.when(loanAccountRepository.findById(1L)).thenReturn(account);
        Mockito.when(loanAccountRepository.checkBeforeProcessing(1L, 1L)).thenReturn(true);
        Mockito.when(creditBureauClient.checkCreditScore("cust-1")).thenReturn(400);

        service.disburseLoan(1L, "cust-1");

        assertEquals(LoanStatus.REJECTED, account.getStatus());
        Mockito.verify(ledgerRepository, Mockito.never()).append(Mockito.any());
    }    

    @Test 
    void refusesDuplicateDisbursementWhenClaimFails() {
        LoanAccount account = new LoanAccount(1L, LoanStatus.PENDING, BigDecimal.valueOf(5000), 0L);
        Mockito.when(loanAccountRepository.findById(1L)).thenReturn(account);
        Mockito.when(loanAccountRepository.checkBeforeProcessing(1L, 0L)).thenReturn(false);

        assertThrows(DuplicateDisbursementException.class, () -> service.disburseLoan(1L, "cust-1"));

        Mockito.verify(creditBureauClient, Mockito.never()).checkCreditScore(Mockito.anyString());
        Mockito.verify(ledgerRepository, Mockito.never()).append(Mockito.any());
    }

    @Test
    void wrapsASlowCreditBureauInATimeout() {
        LoanAccount account = new LoanAccount(1L, LoanStatus.PENDING, BigDecimal.valueOf(5000), 0L);
        Mockito.when(loanAccountRepository.findById(1L)).thenReturn(account);
        Mockito.when(loanAccountRepository.checkBeforeProcessing(1L, 0L)).thenReturn(true);
        Mockito.when(creditBureauClient.checkCreditScore("cust-1")).thenAnswer(invocation -> {
            Thread.sleep(4000); // longer than CREDIT_CHECK_TIMEOUT (3s)
            return 700;
        });

        assertThrows(CreditCheckTimeoutException.class, () -> service.disburseLoan(1L, "cust-1"));
    }    
}
