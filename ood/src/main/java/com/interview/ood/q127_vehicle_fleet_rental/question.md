# q127 — Vehicle Fleet Rental

**Lessons:** [0001 Vehicle Fleet Rental](../../../../../topics/ood/vehicle/0001-vehicle-fleet-rental.html)

**Category:** OOD
**Difficulty:** Medium/Hard
**Domain:** Fleet Rental
**Patterns:** State Machine (enum + canTransitionTo), Strategy (PricingStrategy with duration tiers), Observer (MaintenanceObserver), Value Objects (Money record, RentalPeriod record), Factory

---

## Scenario

Design a vehicle fleet management system for a rental company. Vehicles move through a defined
lifecycle, pricing varies by vehicle class and rental duration, and a maintenance alert fires
when a vehicle's odometer crosses a threshold on return.

---

## Requirements

### 1. Vehicle lifecycle — State Machine

Vehicles move through these states (terminal: `RETIRED`):

```
AVAILABLE → RESERVED → IN_USE → AWAITING_INSPECTION ─┬→ AVAILABLE
                     ↑ (cancel)                        └→ MAINTENANCE → AVAILABLE
AVAILABLE ──────────────────────────────────────────────────────────→ RETIRED
                                                          MAINTENANCE ────────→ RETIRED
```

Transition rules:

| State | Allowed next states |
|---|---|
| `AVAILABLE` | `RESERVED`, `RETIRED` |
| `RESERVED` | `AVAILABLE` (cancel), `IN_USE` (checkout) |
| `IN_USE` | `AWAITING_INSPECTION` only |
| `AWAITING_INSPECTION` | `AVAILABLE` (passed), `MAINTENANCE` (failed) |
| `MAINTENANCE` | `AVAILABLE` (repairComplete), `RETIRED` |
| `RETIRED` | none (terminal) |

Any illegal transition must throw `InvalidStateTransitionException`.

Implement the guard on the enum using `canTransitionTo(VehicleStatus next)`.

---

### 2. Vehicle classes and daily rates (GBP)

| Class | Daily Rate |
|---|---|
| `ECONOMY` | £30 |
| `STANDARD` | £50 |
| `PREMIUM` | £80 |
| `LUXURY` | £130 |

---

### 3. Pricing strategy — duration tiers

| Duration | Multiplier |
|---|---|
| 1–6 days | 1.00 (no discount) |
| 7–27 days | 0.85 (15% off) |
| 28+ days | 0.75 (25% off) |

`Money` must use `BigDecimal`. Never use `double` or `float` for monetary amounts.
Currency is `"GBP"`. Prices are rounded to 2 decimal places (`HALF_UP`).

---

### 4. Maintenance observer — threshold crossing

When `returnVehicle(mileageAtReturn)` is called:
- If the vehicle was **below** the configured threshold at checkout AND **at or above** it at return
  → notify all registered `MaintenanceObserver` instances.
- If the vehicle was already **at or above** the threshold at checkout → do **not** notify
  (a maintenance order was already raised; duplicate notification = duplicate work order).

---

## Classes to implement

| Type | Name | Responsibility |
|---|---|---|
| `enum` | `VehicleStatus` | State machine with `canTransitionTo(VehicleStatus)` guard |
| `enum` | `VehicleClass` | Four classes, each carrying its GBP daily rate |
| `record` | `Money` | Value object — `BigDecimal amount` + `String currency`; `multiply()`, `add()`, `compareTo()` |
| `record` | `RentalPeriod` | Value object — `LocalDate start` + `LocalDate end`; `durationDays()` |
| `interface` | `PricingStrategy` | `Money calculateCost(RentalPeriod period)` |
| `class` | `VehiclePricingStrategy` | Implements `PricingStrategy` with the three duration tiers |
| `class` | `PricingStrategyFactory` | `static PricingStrategy forClass(VehicleClass)` |
| `interface` | `MaintenanceObserver` | `void onMaintenanceRequired(String vehicleId, BigDecimal mileageKm)` |
| `class` | `InvalidStateTransitionException` | Unchecked; carries vehicle ID + from/to states in message |
| `class` | `Vehicle` | Aggregate root — wires state machine + strategy + observer |

---

## Constraints

- `answer/` is empty — implement from scratch
- JUnit 5 + Mockito, no AssertJ
- BigDecimal comparisons in tests: `assertEquals(0, actual.compareTo(expected))`
- 2–4 tests per class — happy path + one meaningful failure
- Open `solution/` and the lesson any time to check your design

---

## Ideal time

30–40 minutes
