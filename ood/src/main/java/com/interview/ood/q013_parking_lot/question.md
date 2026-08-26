# Q013 — OO Design: Parking Lot

**Category:** OO Design  
**Difficulty:** Medium  
**Ideal Time:** 25 min

---

## Problem

Design a Parking Lot system in Java.

---

## Requirements

### Functional
- Parking lot has multiple floors, each floor has multiple spots
- Three spot types: `MOTORCYCLE`, `COMPACT`, `LARGE`
- Three vehicle types: `Motorcycle`, `Car`, `Truck`
- Vehicle fit rules:
  - Motorcycle → fits MOTORCYCLE, COMPACT, or LARGE spot
  - Car        → fits COMPACT or LARGE spot
  - Truck      → fits LARGE spot only
- `park(vehicle)` → finds the nearest available fitting spot, issues a `Ticket`
- `leave(ticket)` → frees the spot, returns fee (flat rate: $2/hr)
- `isFullFor(VehicleType)` → returns true if no fitting spot is available

### Non-Functional
- Thread-safe (multiple entry gates park simultaneously)
- Extensible: adding a new vehicle type or spot type should be minimal change

---

## Classes to Design and Implement

```
Vehicle (abstract)
├── Motorcycle
├── Car
└── Truck

SpotType  (enum: MOTORCYCLE, COMPACT, LARGE)
VehicleType (enum: MOTORCYCLE, CAR, TRUCK)

ParkingSpot
├── id
├── floor
├── spotType
└── isAvailable()

Ticket
├── ticketId
├── vehicle
├── spot
└── entryTime

ParkingLot
├── park(Vehicle) → Ticket
├── leave(Ticket) → double (fee)
└── isFullFor(VehicleType) → boolean
```

---

## What to implement

Write all classes in `MyAnswer.java` (or as separate files in the package — your choice).

Key design decisions to justify in a comment block at the top of your file:
1. How do you find an available fitting spot efficiently?
2. How do you make it thread-safe?
3. Which SOLID principles does your design apply?

---

## Skeleton

See `Solution.java` for interface stubs.  
Implement in `MyAnswer.java`.  
Tests in `MyAnswerTest.java`.
