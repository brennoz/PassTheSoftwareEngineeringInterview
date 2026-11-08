package com.interview.ood.q129_order_management.solution;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// Application service: routes orders to the venue and provides a single registration point
// for global listeners (audit log, risk engine, position tracker, etc.).
//
// Design decision: strategies stored in an EnumMap<OrderType, OrderRoutingStrategy>.
// EnumMap uses the enum ordinal as an array index — O(1) lookup, no hash computation,
// no bucket collision. Correct here because OrderType is a fixed, compile-time-known set.
// Alternative: HashMap. Rejected: correct but no reason to pay the hash overhead when
// an enum-keyed map is available and communicates intent more clearly.
//
// What NOT to do: put strategy selection logic (if/switch on orderType) inside this class —
// adding a new order type then requires editing OrderManager, violating OCP. The map
// approach makes adding a new type a one-liner: strategies.put(STOP_LIMIT, new ...).
public class 
OrderManager {

    private final Map<OrderType, OrderRoutingStrategy> strategies;
    private final List<OrderEventListener> globalListeners = new ArrayList<>();

    public OrderManager() {
        this.strategies = new EnumMap<>(OrderType.class);
        this.strategies.put(OrderType.MARKET, new MarketOrderRoutingStrategy());
        this.strategies.put(OrderType.LIMIT,  new LimitOrderRoutingStrategy());
        this.strategies.put(OrderType.STOP,   new MarketOrderRoutingStrategy()); // STOP triggers at market
    }

    // Package-private constructor allows tests to inject mock strategies without a framework.
    OrderManager(Map<OrderType, OrderRoutingStrategy> strategies) {
        this.strategies = new EnumMap<>(strategies);
    }

    public void registerGlobalListener(OrderEventListener listener) {
        globalListeners.add(listener);
    }

    // Submit wires global listeners onto the order before routing, so no execution or terminal
    // event fires without the listener being attached. Order of operations matters:
    //   1. attach listeners
    //   2. route (transitions NEW → PENDING_ACK)
    //   3. fire onOrderSubmitted (after the status change, consistent with the audit trail)
    public Order submit(Order order) {
        globalListeners.forEach(order::addListener);
        OrderRoutingStrategy strategy = strategies.get(order.getOrderType());
        if (strategy == null)
            throw new IllegalArgumentException(
                "No routing strategy registered for order type: " + order.getOrderType());
        strategy.route(order);                                      // NEW → PENDING_ACK
        globalListeners.forEach(l -> l.onOrderSubmitted(order));
        return order;
    }
}
