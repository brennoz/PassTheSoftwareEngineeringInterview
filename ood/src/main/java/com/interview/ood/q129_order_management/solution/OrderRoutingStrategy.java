package com.interview.ood.q129_order_management.solution;

// Strategy interface — each OrderType routes to the venue differently.
// Why Strategy over a switch in OrderManager: adding STOP_LIMIT or a new venue-specific
// order type requires only a new implementation, not editing OrderManager — OCP applies.
// What NOT to do: embed routing logic in Order.java — the order domain object should not
// know about FIX protocol specifics or network connectivity; that is infrastructure, not domain.
public interface OrderRoutingStrategy {

    // Dispatches the order to the venue connectivity layer.
    // Contract: implementations MUST call order.pendingAck() to transition NEW → PENDING_ACK
    // before returning; callers assume the order is in PENDING_ACK after a successful route().
    void route(Order order);
}
