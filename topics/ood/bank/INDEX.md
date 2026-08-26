# bank — Lesson Index

| File | Concepts covered |
|---|---|
| [0001-domain-model.html](0001-domain-model.html) | Template Method, abstract Account hierarchy, BigDecimal/Money value object, Transaction record, synchronized withdraw/deposit, deadlock-safe transfer |
| [0002-strategy-state.html](0002-strategy-state.html) | Strategy Pattern, InterestCalculationStrategy, SimpleInterest vs CompoundInterest, AccountStatus enum, state guard, SOLID checklist |
| [0003-observer-pattern.html](0003-observer-pattern.html) | Observer Pattern, AccountEventListener, AccountEvent record, CopyOnWriteArrayList, fire-after-state-change, EmailNotificationListener, FraudDetectionListener |
| [0004-bank-service-layer.html](0004-bank-service-layer.html) | Service Layer vs Rich Domain comparison, Account/Transaction as thin records, AccountService owns balance logic, LedgerRepository (append-only), TransferService (no deadlock), InterestStrategy injected into service, production multi-pod issues, AWS mapping |
