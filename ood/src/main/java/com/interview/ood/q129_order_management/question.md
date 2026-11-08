# q129 — Order Management System (OMS)

**Category:** OO Design  
**Difficulty:** Hard  
**Time:** 45–60 min  
**Lessons:** [0001 OMS — State Machine, Strategy, Observer](../../../../../topics/ood/oms/0001-order-management-system.html)

---

## Domain Context

An Order Management System (OMS) sits at the heart of a trading platform. When a trader places an order (e.g. "Buy 100 AAPL at $185.50"), the OMS:
1. Accepts the order and assigns it an ID
2. Routes it to the appropriate venue (exchange, dark pool, broker)
3. Tracks every state change: pending acknowledgement → acknowledged → partial fills → fully filled
4. Publishes events so downstream systems (risk engine, P&L, audit log) react in real time

Your task: model the core OMS domain in pure Java — no Spring, no persistence layer.

---

## Order Lifecycle (state machine)

```
NEW → PENDING_ACK → ACKNOWLEDGED → PARTIALLY_FILLED ⟶ FILLED (terminal)
                 ↘ REJECTED (terminal)   ↘ CANCELLED (terminal)
                                          ↘ EXPIRED (terminal)
```

| Transition | Trigger |
|---|---|
| NEW → PENDING_ACK | OrderManager.submit() dispatches to venue |
| PENDING_ACK → ACKNOWLEDGED | Venue sends an acknowledgement |
| PENDING_ACK → REJECTED | Venue rejects the order (risk limit, bad price, etc.) |
| ACKNOWLEDGED → PARTIALLY_FILLED | Venue sends a partial execution report |
| ACKNOWLEDGED / PARTIALLY_FILLED → FILLED | Final execution: remaining quantity reaches zero |
| ACKNOWLEDGED / PARTIALLY_FILLED → CANCELLED | User or system cancels the live order |
| ACKNOWLEDGED / PARTIALLY_FILLED → EXPIRED | DAY order expires at market close |

---

## Order Types and Time-in-Force

| OrderType | Meaning |
|---|---|
| MARKET | Execute immediately at best available price. No limit price. |
| LIMIT | Execute only at limitPrice or better. Requires a limit price. |
| STOP | Becomes a MARKET order when the stop price is triggered. |

| TimeInForce | Meaning |
|---|---|
| DAY | Cancel unexecuted quantity at market close |
| GTC | Good Till Cancelled — persists across sessions |
| IOC | Immediate Or Cancel — fill available now, cancel rest |
| FOK | Fill Or Kill — fill everything immediately or cancel entirely |

---

## Classes to Implement

```
solution/
├── OrderStatus.java          — enum; abstract canTransitionTo() per constant
├── OrderType.java            — enum; MARKET / LIMIT / STOP; stores FIX tag 40 value
├── TimeInForce.java          — enum; DAY / GTC / IOC / FOK; stores FIX tag 59 value
├── OrderSide.java            — enum; BUY / SELL; stores FIX tag 54 value
├── Price.java                — record; BigDecimal value + currency; validation in compact ctor
├── Execution.java            — record; executionId, executionPrice, filledQuantity, timestamp
├── OrderEventListener.java   — Observer interface; onOrderSubmitted / onExecutionReceived / onOrderTerminated
├── OrderRoutingStrategy.java — Strategy interface; route(Order)
├── MarketOrderRoutingStrategy.java — routes MARKET orders; guards against limit price presence
├── LimitOrderRoutingStrategy.java  — routes LIMIT orders; guards against missing limit price
├── Order.java                — aggregate root; guards all state transitions; calculates remainingQuantity()
├── OrderManager.java         — wires strategies + global listeners; EnumMap<OrderType, Strategy>
└── OrderMain.java            — demo: 5 scenarios including partial fills, rejection, invalid guard
```

---

## Constraints

- `BigDecimal` for all prices and quantities — never `double` or `float`
- `Price` record must validate: non-negative value, non-blank currency
- `Execution` record must validate: positive filledQuantity, non-null timestamp
- `Order.applyExecution()` must auto-transition to FILLED when remaining quantity reaches 0
- `Order.applyExecution()` must throw and roll back the execution if it would overfill
- Invalid transitions must throw `IllegalStateException` — status must not change on a blocked transition
- `Order` uses a static factory (`Order.create()`) — no direct construction
- `OrderManager` stores strategies in an `EnumMap<OrderType, OrderRoutingStrategy>`
- `pendingAck()` is package-private — only routing strategies call it

---

## Patterns to Apply

| Pattern | Where |
|---|---|
| State Machine (enum + abstract method) | `OrderStatus` |
| Strategy | `OrderRoutingStrategy` / `MarketOrderRoutingStrategy` / `LimitOrderRoutingStrategy` |
| Observer | `OrderEventListener` |
| Value Object | `Price`, `Execution` |
| Static Factory | `Order.create()` |

---

## Write here

`answer/` — start from scratch or open `solution/` first, your choice.  
Tests go in `src/test/…/q129_order_management/answer/MyAnswerTest.java`.

Run: `mvn test -pl ood -Dtest="com.interview.ood.q129_order_management.solution.SolutionTest"`
