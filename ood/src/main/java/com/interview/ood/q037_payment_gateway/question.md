**Lessons:** [0001 Payment Gateway OOD](../../../../../topics/ood/payment/0001-payment-gateway.html)

# q037 — Payment Gateway: Opayo Style

**Category:** OOD · **Difficulty:** Medium · **Ideal time:** 35 min

---

## Context

You are building the payment integration layer for a UK e-commerce platform.
Card processing is handled by an external gateway (Opayo / Sage Pay style).
Your job is to design the **domain model and service layer** — not the HTTP client.

---

## Design approach

Use the **Service Layer pattern** (consistent with q036):
- `Payment`, `PaymentRequest` — thin data containers / records
- `PaymentStatus` — enum with embedded state-machine transition guards
- `PaymentGateway` — interface (Strategy); injected into `PaymentService`
- `PaymentService` — owns all lifecycle logic; delegates network call to gateway
- `PaymentRepository` — in-memory `Map`-based store

---

## Requirements

### 1. `Money` (value object)
- Fields: `BigDecimal amount`, `Currency currency`
- Immutable Java record
- Constructor guard: amount must be ≥ 0
- Methods: `add(Money)`, `subtract(Money)` — both validate same currency; `subtract` throws `IllegalArgumentException` if result < 0

### 2. `PaymentRequest` (Builder pattern)
Fields (all `String` unless noted):
- `cardToken` — tokenised card reference (PAN never stored)
- `Money amount`
- `merchantId`
- `merchantReference` — idempotency key (unique per payment attempt)
- `description` (optional, may be null)
- `boolean require3ds` (default `false`)

Provide a static `builder()` factory and an inner `Builder` class.
`build()` must validate that `cardToken`, `amount`, `merchantId`, and `merchantReference` are non-null.

### 3. `PaymentStatus` (state machine)
Enum constants: `INITIATED`, `AUTHORISED`, `CAPTURED`, `SETTLED`, `VOIDED`, `REFUNDED`, `FAILED`

Valid transitions:
- `INITIATED` → `AUTHORISED` or `FAILED`
- `AUTHORISED` → `CAPTURED` or `VOIDED`
- `CAPTURED` → `SETTLED` or `REFUNDED`
- All others → terminal (no valid next state)

Each constant overrides `canTransitionTo(PaymentStatus)`.
Add `assertCanTransitionTo(PaymentStatus)` on the base enum — throws `IllegalStateException` with message `"Cannot transition from X to Y"` if invalid.

### 4. `GatewayResponse` (record)
Fields: `String gatewayTransactionId`, `boolean success`, `String message`

### 5. `PaymentGateway` (interface)
Methods:
- `GatewayResponse authorise(PaymentRequest request)`
- `GatewayResponse capture(String gatewayTransactionId)`
- `GatewayResponse voidPayment(String gatewayTransactionId)`
- `GatewayResponse refund(String gatewayTransactionId, Money amount)`

### 6. `Payment` (record)
Fields: `String id`, `PaymentRequest request`, `PaymentStatus status`, `String gatewayTransactionId` (nullable)

Provide `withStatus(PaymentStatus)` and `withGatewayTxId(String)` — return new record instances (immutable update).

### 7. `PaymentRepository` (interface + in-memory impl)
Interface methods:
- `void save(Payment p)`
- `Optional<Payment> findById(String id)`
- `Payment updateStatus(String id, PaymentStatus status, String gatewayTxId)`

`InMemoryPaymentRepository` uses `HashMap<String, Payment>`.

### 8. `PaymentService`
Constructor: `PaymentService(PaymentGateway gateway, PaymentRepository repo)`

Methods:
- `Payment initiate(PaymentRequest request)` — creates Payment with status=INITIATED, saves to repo
  - **Idempotency:** if a Payment with the same `merchantReference` already exists in the repo, return it immediately without calling the gateway
- `Payment authorise(String paymentId)` — asserts INITIATED→AUTHORISED, calls `gateway.authorise()`, sets status to AUTHORISED (success) or FAILED (failure), saves gateway tx ID
- `Payment capture(String paymentId)` — asserts AUTHORISED→CAPTURED, calls `gateway.capture(gatewayTransactionId)`
- `Payment voidPayment(String paymentId)` — asserts AUTHORISED→VOIDED, calls `gateway.voidPayment(gatewayTransactionId)`
- `Payment refund(String paymentId, Money amount)` — asserts CAPTURED→REFUNDED, calls `gateway.refund(gatewayTransactionId, amount)`
- `Payment findOrThrow(String paymentId)` — throws `IllegalArgumentException("Payment not found: " + id)` if missing

---

## Constraints

- No Spring, no JPA — pure Java 21
- `UUID.randomUUID().toString()` for Payment IDs
- `BigDecimal` everywhere for monetary amounts — never `double`/`float`
- Card PAN must never appear in any class field — only `cardToken`
- No `instanceof` checks or raw types

---

## Enums needed

```java
enum PaymentStatus { INITIATED, AUTHORISED, CAPTURED, SETTLED, VOIDED, REFUNDED, FAILED }
```

---

## Folder layout

```
ood/src/main/java/.../q037_payment_gateway/
├── question.md
├── solution/       ← reference implementation, one class per file (open only after attempting)
└── answer/         ← write your implementation here

ood/src/test/java/.../q037_payment_gateway/
├── solution/SolutionTest.java  ← reference test suite
└── answer/                     ← write your tests here
```

*Hint → `hint` | Full solution → `solution` | Submit → `done`*
