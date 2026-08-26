# Review — Q013 Parking Lot (OO Design)

**Result: ✅ Pass**  
**Tests: 2/2**

---

## What's Good
- Vehicle hierarchy with `fitsIn(SpotType)` — correct abstraction, each subclass owns its own fit rules
- `VehicleFactory` with static factory method — correct pattern
- `ParkingLot` with floors-of-spots structure — models reality well
- `Ticket` with static `AtomicInteger` ID — thread-safe unique IDs ✅
- `leave()` calculates fee with minimum 1hr — correct
- `isFullFor()` uses a probe vehicle to reuse `fitsIn()` logic — clean

---

## Issues

### Design — design decisions block not filled in
The top comment block (finding spot / thread safety / SOLID) was left blank. In a real interview this is where you'd score points — the interviewer expects you to articulate your decisions verbally or in writing.

Answers you should give:
1. **Finding spot:** linear scan floor-by-floor — O(floors × spots). Acceptable for a lot; could use a `Map<SpotType, Queue<ParkingSpot>>` for O(1) lookup.
2. **Thread safety:** `park()` and `leave()` need `synchronized` (or `ReentrantLock`) — two threads could grab the same spot simultaneously. `AtomicInteger` for ticket IDs is correct.
3. **SOLID:** OCP — adding new vehicle type only requires new subclass + `fitsIn()` override, no existing code changes. SRP — each class has one job.

### Bug — `add()` null check never fires
```java
if (floors.get(spot.getFloor()) == null)  // ArrayList never returns null here
```
`ArrayList.get()` throws `IndexOutOfBoundsException` before this check runs. Should validate index range:
```java
if (spot.getFloor() >= floors.size()) throw new IllegalArgumentException(...)
```

### Style — switch can use arrow syntax (Java 14+)
```java
return switch (type) {
    case MOTORCYCLE -> new Motorcycle(licensePlate);
    case CAR        -> new Car(licensePlate);
    case TRUCK      -> new Truck(licensePlate);
};
```

### Style — `fitsIn()` simplify
```java
// instead of:
if (cond) return true;
return false;
// use:
return cond;
```

### Missing — `park()` not thread-safe
Two threads can both pass `spot.isAvailable()` check before either calls `setAvailable(false)` → double-park. Fix: `synchronized(this)` on `park()` and `leave()`.

---

## Score: Pass with reservations
Core design correct. Thread safety not addressed in implementation (only in Ticket ID). Fix `add()` guard and synchronise `park()`/`leave()` before claiming production-ready.
