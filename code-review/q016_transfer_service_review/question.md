# Q016 — Spot the Issues: Fund Transfer Service

**Category:** Code Review (payment domain)  
**Difficulty:** Medium  
**Ideal Time:** 15 min

---

## Context

This service handles fund transfers between accounts on a banking platform. It runs on **3 ECS instances** behind an ALB, backed by **RDS Aurora Multi-AZ**. Find all issues and explain the consequence in this distributed AWS environment.

---

## Code

```java
// TransactionController.java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<Account> transfer(@RequestBody TransferRequest request) {
        Account account = transactionService.transfer(request);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/history/{accountId}")
    public List<Transaction> getHistory(@PathVariable String accountId) {
        return transactionService.getHistory(accountId);
    }
}

// TransactionService.java
@Service
@Slf4j
public class TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuditService auditService;   // calls external audit API

    public Account transfer(TransferRequest request) {
        Account from = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Account to = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (from.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient funds");
        }

        from.setBalance(from.getBalance() - request.getAmount());
        to.setBalance(to.getBalance() + request.getAmount());

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction tx = new Transaction(
                request.getFromAccountId(), request.getToAccountId(), request.getAmount());
        transactionRepository.save(tx);

        auditService.log(tx);   // synchronous call to external audit API

        return from;
    }

    public List<Transaction> getHistory(String accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        List<Transaction> result = new ArrayList<>();
        for (Transaction tx : transactions) {
            if (tx.getStatus() == "COMPLETED") {
                Account account = accountRepository.findById(tx.getFromAccountId()).get();
                tx.setAccountName(account.getName());
                result.add(tx);
            }
        }
        return result;
    }
}
```

---

## Write your answer in `MyAnswer.md`

For each issue:
```
### N. [Category] — short title
Line(s): where
What is wrong: ...
Consequence on this 3-node AWS banking system: ...
Fix: ...
```

Categories: Data integrity | Java bug | Security/API design | Performance | Spring anti-pattern | Distributed pitfall
