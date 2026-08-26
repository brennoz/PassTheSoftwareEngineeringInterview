**Lessons:** [0001 Idempotent Payment API](../../../topics/system-design/idempotent-payment-api/0001-idempotent-payment-api.html)

# q033 — Design an Idempotent Payment API

**Category:** System Design · **Difficulty:** Medium-Hard · **Domain:** Payments/Fintech · **Ideal time:** 30 min

---

## Scenario

You are designing the payment processing API for a fintech company.
Clients (mobile apps, merchants) call `POST /payments` to charge a customer.
Mobile networks are unreliable — clients will retry on timeout or 5xx.
Without protection, a retry causes a double charge.

Your API must be **safe to retry** — the same payment must never be processed more than once.

---

## Requirements

1. Client sends `Idempotency-Key: <uuid>` header with every payment request.
2. Retrying the exact same request returns the same result as the first attempt — no double charge.
3. Two concurrent retries with the same key must not both process the payment.
4. If a client reuses the same key with a different payload (different amount), return an error.
5. After a successful payment, a `PaymentCompleted` event must be published to downstream services (fraud detection, notifications).
6. Event publishing must be reliable — a Kafka outage must not lose the event.
7. Idempotency records expire after 24 hours.

---

## Scope your answer around these sections

### 1. Idempotency key design
- Who generates it, what format, which HTTP header, what TTL?

### 2. Server-side algorithm
- Step-by-step flow: what does the server do on first request? On retry?
- What does "PROCESSING" status protect against?

### 3. Concurrent duplicate handling
- Two requests arrive simultaneously, key not in DB. Walk through the race condition.
- What DB primitive prevents the double charge? Why not SELECT FOR UPDATE?

### 4. Request hash validation
- Why store a hash of the request body? What do you return on mismatch?

### 5. Reliable event publishing
- Why can't you publish to Kafka directly after DB commit?
- Describe the Outbox Pattern and how the relay works.

### 6. Data model
- Sketch the three tables: `idempotency_records`, `payments`, `payment_outbox`.

### 7. AWS / distributed environment
- Map each component to an AWS service.
- How does DynamoDB conditional write replace the SQL unique constraint?
- What breaks on a multi-pod ECS deployment without this design?

---

## Write your answer

`MyAnswer.md` in this folder.

---

*Hint → `hint` | Submit → `done`*
