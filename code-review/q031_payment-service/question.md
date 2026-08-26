# q031 — Spot the Issue: Payment Service

**Category:** Code Review · **Difficulty:** Medium · **Domain:** Payments / Fintech · **Ideal time:** 15 min

---

## Context

Junior dev pushed this Spring Boot payment service to production last week.
Payments are being processed, but the team is seeing occasional duplicate charges,
random balance inconsistencies under load, and a compliance alert from the security team.

Find everything wrong. Some issues are subtle.

---

## Code

```java
@Service
public class PaymentService {

    private final AccountRepository  accountRepository;
    private final PaymentRepository  paymentRepository;
    private final Map<String, Payment> recentPayments = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(AccountRepository accountRepository,
                          PaymentRepository paymentRepository) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
    }

    public PaymentResult processPayment(String accountId,
                                        String cardNumber,
                                        double amount,
                                        String idempotencyKey) {

        log.info("Processing payment: card={}, amount={}", cardNumber, amount);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        if (account.getBalance() < amount) {
            return PaymentResult.insufficientFunds();
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Payment payment = new Payment(accountId, amount, LocalDateTime.now());
        paymentRepository.save(payment);

        recentPayments.put(idempotencyKey, payment);

        return PaymentResult.success(payment.getId());
    }

    public double calculateFee(double amount, double feePercent) {
        return amount * feePercent / 100;
    }
}
```

---

## Your task

List every issue you find. For each one:

```
### N. [Category] — [short title]
Line: X
What's wrong: ...
Consequence in production: ...
Fix: ...
```

Write your answer in `MyAnswer.md` in this folder.

---

*Hint available → type `hint` | Submit → type `done`*
