# Q027 — Bank Account Notifications

**Category:** OO Design  
**Difficulty:** Medium  
**Ideal time:** 20 min  
**Lessons:** [0001 Domain Model](../../../../../topics/ood/bank/0001-domain-model.html) · [0002 Strategy + State](../../../../../topics/ood/bank/0002-strategy-state.html) · [0003 Observer Pattern](../../../../../topics/ood/bank/0003-observer-pattern.html)

---

## Problem

Extend a `BankAccount` system to support pluggable event listeners using the **Observer Pattern**.

### Requirements

**Event system:**
- `BankAccount` fires an `AccountEvent` after each: `deposit`, `withdrawal`, `close`
- Any number of listeners can be registered or removed at runtime
- `BankAccount` must not depend on any concrete listener class — only on the interface

**Implement two concrete listeners:**
- `EmailNotificationListener` — prints a message simulating email notification on every event
- `FraudDetectionListener` — logs a fraud alert when a withdrawal exceeds a configurable threshold (default: 10,000)

**Thread safety:**
- The listener list must be safe for concurrent registration/removal while events fire

### Rules
- Adding a new listener type must require zero changes to `BankAccount`
- Fire events **after** state change — listeners must see updated balance
- `FraudDetectionListener` threshold must be configurable via constructor

---

## Class Diagram

```
«interface»
AccountEventListener
  + onEvent(AccountEvent): void
       ▲                 ▲
       │                 │
EmailNotification    FraudDetection
  Listener             Listener

«record»
AccountEvent
  - type: EventType  (DEPOSIT | WITHDRAWAL | CLOSE)
  - accountId: String
  - amount: BigDecimal
  - balanceAfter: BigDecimal
  - occurredAt: Instant

«abstract»
BankAccount
  - listeners: List<AccountEventListener>
  + addListener(listener)
  + removeListener(listener)
  # fireEvent(event)           ← called after each state change
  + deposit(amount)
  + withdraw(amount)
  + close()

SavingsAccount         CheckingAccount
  (from q026)            (from q026)
```

---

## What to implement

Fill in `MyAnswer.java`. You may define all classes in one file.

Write at least 2 tests in `MyAnswerTest.java`:
- `EmailNotificationListener` receives event on deposit
- `FraudDetectionListener` fires alert when withdrawal exceeds threshold
- `FraudDetectionListener` does NOT fire for normal withdrawal
