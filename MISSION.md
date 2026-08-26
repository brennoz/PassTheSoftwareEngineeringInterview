# Mission: Object-Oriented Design — Bank System in Java

## Why
Pass the OOD category of software engineering interviews. The bank system is a canonical
interview problem that probes domain modelling, inheritance hierarchies, design patterns,
and thread safety — all things that appear repeatedly in senior engineer interviews at
fintech and banking companies.

## Success looks like
- Given "design a bank system", produce a clean class diagram and Java skeleton within 25 minutes
- Name the correct design pattern for each design decision and justify it
- Identify where thread safety is required and apply `@Version` / `synchronized` / `AtomicLong` correctly
- Extend the design without modifying existing classes (Open/Closed)
- Spot OOD violations in code review questions (missing abstraction, broken SRP, wrong pattern)

## Constraints
- Interview simulation context — no Spring, no Hibernate, pure Java 21
- Each lesson completable in < 20 minutes
- Prior OOD: passed Parking Lot (q013) — understands basic abstraction and interfaces

## Out of scope
- Persistence layer (JPA, Hibernate) — this is domain modelling only
- Spring Security / JWT — covered separately in project 09
- Full microservices decomposition — this is single-system OOD
