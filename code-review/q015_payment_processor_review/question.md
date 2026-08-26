# Q015 — Spot the Issues: Payment Processor

**Category:** Code Review (Visa domain)  
**Difficulty:** Medium  
**Ideal Time:** 15 min

---

## Context

This service processes card payments at Visa. It runs on **4 ECS tasks across 2 AWS regions** (us-east-1 active, eu-west-1 standby). It handles ~5,000 transactions/second at peak.

Find all issues. For each, explain the consequence in this specific environment — a 4-node distributed system in AWS processing real card payments.

---

## Code

```java
@Service
@Slf4j
public class PaymentProcessor {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private SnsClient snsClient;

    private static final String SNS_TOPIC =
            "arn:aws:sns:us-east-1:123456789012:payment-events";

    public PaymentResult processPayment(PaymentRequest request) {
        log.info("Processing payment: card={}, amount={}, merchant={}",
                request.getCardNumber(), request.getAmount(), request.getMerchantId());

        Account account = accountRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance() < request.getAmount()) {
            return PaymentResult.INSUFFICIENT_FUNDS;
        }

        account.setBalance(account.getBalance() - request.getAmount());
        accountRepository.save(account);

        Payment payment = new Payment(
                request.getCardNumber(), request.getAmount(), new Date());
        payment.setStatus("PENDING");
        paymentRepository.save(payment);

        snsClient.publish(PublishRequest.builder()
                .topicArn(SNS_TOPIC)
                .message(payment.getId().toString())
                .build());

        if (payment.getStatus() == "PENDING") {
            payment.setStatus("COMPLETED");
            paymentRepository.save(payment);
        }

        return PaymentResult.SUCCESS;
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
Consequence on this 4-node AWS payment system: ...
Fix: ...
```

Categories: Security/PCI-DSS | Java bug | Data integrity | Distributed pitfall | AWS config | Spring anti-pattern
