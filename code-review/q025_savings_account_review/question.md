# Q025 — Savings Account Service Review

**Category:** Code Review — Banking Domain  
**Difficulty:** Easy  
**Ideal time:** 12 min

---

## Question

Spot every issue in this savings account service. For each issue state:
- Category
- What is wrong (line reference)
- Real-world consequence in prod / at scale
- Fix

---

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class SavingsAccountService {

    @Autowired
    private SavingsAccountRepository repository;

    public SavingsAccount getAccount(Long accountId) {               // line 12
        return repository.findById(accountId).get();                 // line 13
    }

    public SavingsAccount applyMonthlyInterest(Long accountId) {

        SavingsAccount account = repository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

        double balance  = account.getBalance().doubleValue();        // line 21
        double rate     = account.getAnnualRate().doubleValue();
        double interest = balance * (rate / 12.0);                   // line 23

        account.setBalance(
            account.getBalance().add(new BigDecimal(interest))       // line 26
        );

        return repository.save(account);                             // line 28
    }
}
```

---

Write your answer in `MyAnswer.md`.
