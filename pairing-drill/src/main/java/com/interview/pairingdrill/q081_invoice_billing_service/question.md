# q081 — Invoice Billing Service Refactor

**Category:** pairing-drill | **Flavor:** Type safety / primitive obsession + Service smells | **Difficulty:** medium (4–5 planted issues) | **Domain:** Billing (BillingCo)
**Partner seniority:** junior

**Lessons:** [0002 Java OOP & Code Review](../../../../../topics/projects/billingco/0002-java-oop-multithreading-code-review.html) | [0005 Architecture & Redis](../../../../../topics/projects/billingco/0005-microservices-redis-architecture.html)

---

## Scenario

You're pairing with Alex, a junior developer on the billing team. Alex has just pushed a first draft of the `InvoiceBillingService` — the core service that creates, issues, and applies payments to invoices.

The service passes its unit tests but you've spotted several issues before the PR is merged. Your goal is to walk Alex through the problems, explain the production consequences, and guide the refactor.

Alex will ask questions as you work. Try to explain the *why*, not just the *what* — Alex wants to understand, not just copy your answers.

---

## The Code

The seeded code is in `InvoiceBillingService.java` (reference — do not edit).
Work in `answer/InvoiceBillingService.java`.

Supporting types:
- `Invoice.java` — the domain entity
- `InvoiceRepository.java` — JPA repository
- `PaymentGatewayClient.java` — external payment gateway

---

## Your Task

1. Read `InvoiceBillingService.java` and identify all problems
2. Refactor `answer/InvoiceBillingService.java` with the fixes
3. Write tests in `answer/MyAnswerTest.java` (JUnit 5 + Mockito only — no AssertJ)
4. Use `review` to get feedback from Alex during the session
5. Use `done` when complete

---

## What to look for

Issues span at least 2 categories. Think about:
- Type correctness for monetary values
- Status representation and comparison
- Thread safety on shared state
- Transaction boundaries
- Exception handling

---

## Ideal time: 45 minutes
