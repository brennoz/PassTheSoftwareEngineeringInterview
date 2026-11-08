# q128 — Invoice Payment Service (Spring Boot REST API)

**Lesson / Prep:** [BillingCo Interview Prep](../../../../../topics/projects/billingco/0001-billingco-interview-prep.html)

**Category:** Spring / OOD
**Difficulty:** Medium/Hard
**Domain:** Billing & Payments (Telecoms)
**Patterns:** State Machine (InvoiceStatus), Repository (JpaRepository), Service Layer (@Transactional), REST API (@RestController), Input Validation (@Valid), Global Error Handling (@ControllerAdvice)

---

## Context

BillingCo builds telecoms billing software. Their interview coding exercise is a working Spring Boot
REST API — the interviewer watches you build it and may ask you to explain every annotation and
design decision. This question drills exactly that exercise.

---

## Scenario

Build a backend for managing invoices and recording payments.

---

## Domain Model

### Invoice lifecycle (state machine)

```
DRAFT ──→ ISSUED ──→ PARTIALLY_PAID ──→ PAID
                 └──→ PAID                ↑
                 └──→ OVERDUE ────────────┘
                 └──→ CANCELLED
DRAFT ──→ CANCELLED
PARTIALLY_PAID ──→ CANCELLED
OVERDUE ──→ CANCELLED
```

Rules:
- `DRAFT` → `ISSUED`, `CANCELLED`
- `ISSUED` → `PARTIALLY_PAID`, `PAID`, `OVERDUE`, `CANCELLED`
- `PARTIALLY_PAID` → `PAID`, `OVERDUE`, `CANCELLED`
- `OVERDUE` → `PAID`, `PARTIALLY_PAID`, `CANCELLED`
- `PAID`, `CANCELLED` — terminal, no further transitions
- Invalid transition → throw `InvalidStateTransitionException` → HTTP 409

### Invoice fields

| Field | Type | Notes |
|---|---|---|
| `id` | `String` | UUID, generated on create |
| `customerId` | `String` | Required |
| `status` | `InvoiceStatus` | Starts DRAFT |
| `lineItems` | `List<InvoiceLineItem>` | One-to-many |
| `paidAmount` | `BigDecimal` | Starts 0.00, accumulates on payments |
| `issueDate` | `LocalDate` | Set when issued |
| `dueDate` | `LocalDate` | Required, set on create |

### Computed fields (methods on Invoice)

- `totalAmount()` — sum of all line item subtotals
- `amountDue()` — `totalAmount() - paidAmount`

### InvoiceLineItem fields

| Field | Type |
|---|---|
| `description` | `String` |
| `unitPrice` | `BigDecimal` |
| `quantity` | `int` |
| `subtotal()` | `BigDecimal` — `unitPrice × quantity` |

---

## REST API

| Method | Path | Body | Response | Status |
|---|---|---|---|---|
| `POST` | `/api/invoices` | `CreateInvoiceRequest` | `InvoiceResponse` | 201 |
| `GET` | `/api/invoices/{id}` | — | `InvoiceResponse` | 200 |
| `GET` | `/api/invoices?customerId={id}` | — | `List<InvoiceResponse>` | 200 |
| `POST` | `/api/invoices/{id}/line-items` | `AddLineItemRequest` | `InvoiceResponse` | 200 |
| `POST` | `/api/invoices/{id}/issue` | — | `InvoiceResponse` | 200 |
| `POST` | `/api/invoices/{id}/payments` | `RecordPaymentRequest` | `InvoiceResponse` | 200 |
| `POST` | `/api/invoices/{id}/cancel` | — | `InvoiceResponse` | 200 |
| `DELETE` | `/api/invoices/{id}` | — | — | 204 |

---

## DTOs

### CreateInvoiceRequest
- `customerId` — `@NotBlank`
- `dueDate` — `@NotNull`

### AddLineItemRequest
- `description` — `@NotBlank`
- `unitPrice` — `@NotNull @DecimalMin("0.01")`
- `quantity` — `@Min(1)`

### RecordPaymentRequest
- `amount` — `@NotNull @DecimalMin("0.01")`
- Guard: overpayment (amount > amountDue) → throw `IllegalArgumentException` → HTTP 400

### InvoiceResponse
A record mapping all invoice fields. Include a nested `LineItemResponse` record.

---

## Error Handling

`GlobalExceptionHandler` (`@RestControllerAdvice`):

| Exception | HTTP status |
|---|---|
| `InvoiceNotFoundException` | 404 |
| `InvalidStateTransitionException` | 409 |
| `IllegalArgumentException` | 400 |
| `MethodArgumentNotValidException` | 400 (list of field errors) |

---

## Classes to implement

| Class | Layer | Key detail |
|---|---|---|
| `InvoiceStatus` | domain | Enum state machine with `canTransitionTo()` |
| `Invoice` | domain / `@Entity` | Aggregate root — `issue()`, `applyPayment()`, `addLineItem()`, `cancel()` |
| `InvoiceLineItem` | domain / `@Entity` | `subtotal()` |
| `InvoiceRepository` | repository | `JpaRepository<Invoice, String>` + `findByCustomerId` |
| `InvoiceService` | service | `@Service @Transactional` — all business rules live here |
| `InvoiceController` | api | `@RestController` — HTTP translation only, no business logic |
| `CreateInvoiceRequest` | api/dto | Record with `@NotBlank`, `@NotNull` |
| `AddLineItemRequest` | api/dto | Record with validation |
| `RecordPaymentRequest` | api/dto | Record with validation |
| `InvoiceResponse` | api/dto | Record with nested `LineItemResponse`, static `from()` factory |
| `GlobalExceptionHandler` | api | `@RestControllerAdvice` |
| `InvoiceNotFoundException` | exception | extends `RuntimeException` |
| `InvalidStateTransitionException` | exception | extends `RuntimeException` |

---

## Constraints

- `answer/` is empty — implement from scratch
- JUnit 5 + Mockito only for tests — no Spring context / no `@SpringBootTest`
- `BigDecimal` only for monetary amounts — never `double`
- Constructor injection only — no `@Autowired` field injection
- `@Transactional(readOnly = true)` on read methods

---

## Ideal time

45–60 minutes (matches real BillingCo coding exercise pace)
