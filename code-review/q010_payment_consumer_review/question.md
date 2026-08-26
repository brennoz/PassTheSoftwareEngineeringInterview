# Q010 — Spot the Issues: Kafka Payment Consumer

**Category:** Code Review  
**Difficulty:** Medium  
**Ideal Time:** 15 min

---

## Task

This Kafka consumer processes payment events on a production system handling 50k transactions/day. Find everything wrong — Spring issues, Kafka pitfalls, security problems, Java bugs, anything that could cause failures or data corruption in production.

---

## Code

```java
@Component
@Slf4j
public class PaymentEventConsumer {

    private static int processedCount = 0;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FraudCheckClient fraudCheckClient;  // calls external fraud API via HTTP

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "payments", groupId = "payment-processor")
    @Transactional
    public void onPayment(PaymentEvent event) {
        processedCount++;

        log.info("Processing payment: id={}, card={}, amount={}, user={}",
                event.getId(), event.getCardNumber(), event.getAmount(), event.getUserId());

        try {
            boolean isFraud = fraudCheckClient.check(event);

            Payment payment = new Payment(event.getId(), event.getAmount(), event.getUserId());

            if (event.getStatus() == "PENDING") {
                payment.setStatus(isFraud ? "REJECTED" : "APPROVED");
            }

            paymentRepository.save(payment);
            emailService.sendReceipt(event.getUserId(), event.getAmount());

        } catch (Exception e) {
            log.error("Failed: " + e.getMessage());
        }
    }
}
```

---

## Write your answer in `MyAnswer.md`

For each issue:
```
### N. [Category] — short title
Line(s): X
What is wrong: ...
Consequence in prod: ...
Fix: ...
```

Categories: Spring anti-pattern | Kafka | Concurrency | Security | Java bug | Exception handling
