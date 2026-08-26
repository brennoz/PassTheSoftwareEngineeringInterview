# q038 — Code Review: Loan Disbursement Service

**Category:** Code Review · **Difficulty:** Medium · **Ideal time:** 15 min  
**Domain:** Banking

---

## Task

Find every bug, design flaw, and production risk in the snippet below.
List each issue with: category, what is wrong, consequence, fix.

The number of planted issues is not revealed upfront.

---

## Code Under Review

```java
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanDisbursementService loanService;

    @GetMapping("/{id}/approve")
    public LoanApplication approveLoan(@PathVariable Long id,
                                       @RequestParam double amount) {
        return loanService.approve(id, amount);
    }

    @PostMapping("/{id}/disburse")
    public void disburseLoan(@PathVariable Long id) {
        loanService.disburse(id);
    }
}

@Service
public class LoanDisbursementService {

    @Autowired
    private LoanRepository loanRepo;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private AuditLogger auditLogger;

    public LoanApplication approve(Long loanId, double approvedAmount) {
        LoanApplication loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));

        if (loan.getStatus() == "PENDING") {
            loan.setStatus("APPROVED");
            loan.setApprovedAmount(approvedAmount);
            loanRepo.save(loan);
        }

        return loan;
    }

    public void disburse(Long loanId) {
        LoanApplication loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + loanId));

        if (loan.getStatus() == "APPROVED") {
            Account account = accountRepo.findByOwnerId(loan.getApplicantId());
            account.setBalance(account.getBalance() + loan.getApprovedAmount());
            accountRepo.save(account);

            loan.setStatus("DISBURSED");
            loanRepo.save(loan);
        }
    }

    public List<LoanApplication> getPendingLoans() {
        return loanRepo.findAll();
    }
}
```

---

## Answer format

Write in `MyAnswer.md`:

```
## Issues Found

### 1. [Category] — [short title]
Line ~X: [what is wrong]
Consequence: [what breaks in prod]
Fix: [what to change]

### 2. ...
```

*Hint → `hint` | Submit → `done`*
