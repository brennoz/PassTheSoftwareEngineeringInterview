package com.interview.pairingdrill.q076_loan_disbursement.answer;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class LoanDisbursementService {

    private static final Duration CREDIT_CHECK_TIMEOUT = Duration.ofSeconds(3);

    private final LoanAccountRepository loanAccountRepository;
    private final LedgerRepository ledgerRepository;
    private final CreditBureauClient creditBureauClient;


    public LoanDisbursementService(LoanAccountRepository loanAccountRepository,
                                    LedgerRepository ledgerRepository,
                                    CreditBureauClient creditBureauClient) {
        this.loanAccountRepository = loanAccountRepository;
        this.ledgerRepository = ledgerRepository;
        this.creditBureauClient = creditBureauClient;
    }    

    @Transactional 
    public void disburseLoan(Long loanAccountId, String customerId) {

        LoanAccount account = loanAccountRepository.findById(loanAccountId);

        if (!loanAccountRepository.checkBeforeProcessing(loanAccountId, account.getVersion())) {
            throw new DuplicateDisbursementException(loanAccountId);
        }

        int creditScore = checkCreditScoreWithTimeout(customerId);
        
        if (creditScore < 600) {
            account.setStatus(LoanStatus.REJECTED);
            loanAccountRepository.save(account);
            return;
        }

        account.setStatus(LoanStatus.APPROVED);
        loanAccountRepository.save(account);

        ledgerRepository.append(new LedgerEntry(loanAccountId, "DISBURSEMENT", account.getPrincipal()));

        account.setStatus(LoanStatus.DISBURSED);
        loanAccountRepository.save(account);
    }

    private int checkCreditScoreWithTimeout(String customerId) {
        try {
            return CompletableFuture.supplyAsync(() -> creditBureauClient.checkCreditScore(customerId))
                    .get(CREDIT_CHECK_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new CreditCheckTimeoutException(customerId);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new CreditCheckTimeoutException(customerId);
        }
    }
}
