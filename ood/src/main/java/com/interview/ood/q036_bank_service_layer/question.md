**Lessons:**
- [0001 Rich Domain Model](../../../../../topics/ood/bank/0001-domain-model.html)
- [0004 Service Layer vs Rich Domain](../../../../../topics/ood/bank/0004-bank-service-layer.html)

# q036 — Bank System: Service Layer Pattern

**Category:** OOD · **Difficulty:** Medium · **Ideal time:** 30 min

---

## Design approach

Re-implement the bank account system from q026 using the **Service Layer pattern**:

- `Account`, `Transaction`, `Transfer` are **thin records** — no behavior, no balance logic
- `AccountRepository` stores accounts + tracks balances (`Map<id, BigDecimal>`)
- `LedgerRepository` records every transaction (append-only, no delete)
- `AccountService` owns all balance logic: deposit, withdraw, close, getBalance
- `TransferService` orchestrates cross-account transfers (calls AccountService twice)
- `BankService` is the facade used by tests

Compare your design choices to q026 after finishing.

---

## Requirements

1. `Account(id, ownerId, AccountType, AccountStatus)` — Java record
2. `Transaction(id, accountId, TransactionType, BigDecimal amount, BigDecimal balanceAfter, Instant createdAt)` — Java record
3. `Transfer(id, fromAccountId, toAccountId, BigDecimal amount, Instant createdAt)` — Java record
4. `AccountRepository`:
   - `save(Account)` — create or replace
   - `findById(String)` → `Optional<Account>`
   - `addToBalance(String accountId, BigDecimal amount)` → `BigDecimal` (new balance)
   - `subtractFromBalance(String accountId, BigDecimal amount)` → `BigDecimal` (new balance)
   - `getBalance(String accountId)` → `BigDecimal`
5. `LedgerRepository`:
   - `record(Transaction)` — append only, never delete
   - `history(String accountId)` → `List<Transaction>` (unmodifiable)
6. `AccountService`:
   - `open(String ownerId, AccountType type)` → `Account`
   - `deposit(String accountId, BigDecimal amount)` → `Transaction`
   - `withdraw(String accountId, BigDecimal amount)` → `Transaction` (throws `InsufficientFundsException` if balance < amount)
   - `getBalance(String accountId)` → `BigDecimal`
   - `close(String accountId)` — throws `IllegalStateException` if balance > 0; replaces account record with status=CLOSED
7. `TransferService`:
   - `transfer(String fromId, String toId, BigDecimal amount)` → `Transfer`
   - Throws `IllegalArgumentException` if fromId.equals(toId)
   - Throws `InsufficientFundsException` if from account has insufficient balance
8. `BankService` (facade):
   - `register(String ownerId)` — no-op (ownerId is just a string here)
   - `openAccount(String ownerId, AccountType type)` → `Account`
   - `deposit(String accountId, BigDecimal amount)` → `Transaction`
   - `withdraw(String accountId, BigDecimal amount)` → `Transaction`
   - `transfer(String fromId, String toId, BigDecimal amount)` → `Transfer`
   - `getBalance(String accountId)` → `BigDecimal`
   - `history(String accountId)` → `List<Transaction>`
   - `closeAccount(String accountId)`

---

## Constraints

- No Spring, no JPA, pure Java 21
- `UUID.randomUUID().toString()` for IDs
- `BigDecimal` for all monetary amounts — never `double` or `float`
- No `instanceof` or type-switch
- `AccountRepository.subtractFromBalance()` must check balance >= amount before subtracting (throw `InsufficientFundsException` if not)

---

## Enums needed

```java
enum AccountType   { CURRENT, SAVINGS }
enum AccountStatus { ACTIVE, CLOSED }
enum TransactionType { CREDIT, DEBIT }
```

---

## Skeleton

`MyAnswer.java` in this folder.

*Hint → `hint` | Submit → `done`*
