# Q017 — Payment Endpoint Review

**Category:** Code Review  
**Difficulty:** Easy  
**Ideal time:** 12 min

---

## Question

Spot every issue in this payment REST controller. For each issue state:
- Category
- What is wrong (line reference)
- Real-world consequence in prod / at scale
- Fix

---

```java
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PaymentService paymentService;

    // POST /api/v1/payments/transfer
    @PostMapping("/transfer")
    public Account transfer(@RequestBody TransferRequest request) {

        Optional<Account> fromOpt = accountRepository.findById(request.getFromAccountId());
        Account from = fromOpt.get();   // line 20

        Optional<Account> toOpt = accountRepository.findById(request.getToAccountId());
        Account to = toOpt.get();       // line 23

        paymentService.transfer(from, to, request.getAmount());

        return from;    // line 26
    }

    // GET /api/v1/payments/{accountId}/balance
    @GetMapping("/{accountId}/balance")
    public Account getBalance(@PathVariable Long accountId) {
        return accountRepository.findById(accountId).get();   // line 32
    }
}
```

---

Write your answer in `MyAnswer.md`.
