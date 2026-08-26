# Q015 — My Answer: Payment Processor

Write each issue below using the format from question.md.

1. Logging card number and merchant is wrong for security reasons
2. Everything should be in a single Transaction 
3. payment.getStatus() == "PENDING" should be equals or equalsIgnoreCase
4. Missing constructor for service injection
5.         snsClient.publish(PublishRequest.builder()
                .topicArn(SNS_TOPIC)
                .message(payment.getId().toString())
                .build()); maybe it should be invoked in a separate thread?
6. publication is happening only in us:region

---

