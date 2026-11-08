# oms — Lesson Index

| File | Concepts covered |
|---|---|
| 0001-order-management-system.html | Order lifecycle state machine (enum abstract canTransitionTo), data-carrying enums (OrderType/TimeInForce with FIX tags), value objects (Price record BigDecimal, Execution record), Strategy (OrderRoutingStrategy / MarketOrderRoutingStrategy / LimitOrderRoutingStrategy), Observer (OrderEventListener), Order aggregate (partial fills, auto-transition to FILLED, overfill guard), OrderManager (EnumMap strategies), trading domain vocabulary |
