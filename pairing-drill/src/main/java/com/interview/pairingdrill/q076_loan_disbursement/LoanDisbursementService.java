package com.interview.pairingdrill.q076_loan_disbursement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanDisbursementService {

    @Autowired
    private LoanAccountRepository loanAccountRepository;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private CreditBureauClient creditBureauClient;

    public void disburseLoan(Long loanAccountId, String customerId) {
        LoanAccount account = loanAccountRepository.findById(loanAccountId);

        int creditScore = creditBureauClient.checkCreditScore(customerId);
        if (creditScore < 600) {
            account.setStatus("REJECTED");
            loanAccountRepository.save(account);
            return;
        }

        account.setStatus("APPROVED");
        loanAccountRepository.save(account);

        ledgerRepository.append(new LedgerEntry(loanAccountId, "DISBURSEMENT", account.getPrincipal()));

        account.setStatus("DISBURSED");
        loanAccountRepository.save(account);
    }
}
