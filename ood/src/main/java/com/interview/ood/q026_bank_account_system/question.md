# Q026 — Bank Account System

**Category:** OO Design  
**Difficulty:** Medium  
**Ideal time:** 25 min  
**Lessons:** [0001 Domain Model](../../../../../topics/ood/bank/0001-domain-model.html) · [0002 Strategy + State](../../../../../topics/ood/bank/0002-strategy-state.html)

---

## Problem

Design a bank account system with the following requirements:

### Accounts
- `SavingsAccount` — earns interest; has a minimum balance requirement; cannot overdraft
- `CheckingAccount` — no interest; has an overdraft limit (can go negative up to the limit)
- Both share common behaviour: `deposit()`, `withdraw()`, `getBalance()`, `getTransactionHistory()`

### Interest Calculation
- Interest calculation strategy must be **swappable** without changing account code
- Implement two strategies:
  - `SimpleInterestStrategy` — `balance × rate × time`
  - `CompoundInterestStrategy` — `balance × (1 + rate)^time - balance`
- `SavingsAccount` delegates interest calculation to whichever strategy is injected

### Transactions
- Every deposit and withdrawal creates an immutable `Transaction` record
- `Transaction` holds: type (`DEPOSIT` / `WITHDRAWAL`), amount, resulting balance, timestamp
- Transaction history is append-only — no modification after creation

### Rules
- `withdraw()` on `SavingsAccount` must reject if balance would drop below minimum balance
- `withdraw()` on `CheckingAccount` must reject if balance would drop below `-overdraftLimit`
- `deposit()` and `withdraw()` must use `BigDecimal` — never `double`
- Closed accounts must reject all operations

---

## Class Diagram (sketch)

```
«interface»
InterestCalculationStrategy
  + calculate(balance, rate, periods): BigDecimal

SimpleInterestStrategy   CompoundInterestStrategy
  (implements above)         (implements above)

«abstract»
BankAccount
  # balance: BigDecimal
  # transactions: List<Transaction>
  + deposit(amount)
  + withdraw(amount)     ← abstract rules per subclass
  + getBalance()
  + getTransactionHistory()
  + close()

SavingsAccount                CheckingAccount
  - minimumBalance            - overdraftLimit
  - annualRate                + withdraw(amount)  ← allows negative to limit
  - strategy: InterestCalculationStrategy
  + applyInterest(periods)
  + withdraw(amount)    ← rejects below minimum
```

---

## What to implement

Fill in `MyAnswer.java`. You may use multiple top-level classes in one file, or split into inner classes — your choice.

Write at least 2 tests in `MyAnswerTest.java`:
- Happy path: deposit + withdraw on both account types
- Rule enforcement: SavingsAccount rejects below-minimum withdrawal

Implement in `MyAnswer.java`.
