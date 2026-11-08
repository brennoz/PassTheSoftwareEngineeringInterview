package com.interview.ood.q129_order_management.solution;

// Limit order: execute only at limitPrice or better.
// Validation here (not in Order.java) because the routing strategy is the only caller that
// needs to know the FIX semantics of a missing price — Order.java enforces presence at
// construction time for LIMIT orders; this guard is a belt-and-suspenders defence.
public class LimitOrderRoutingStrategy implements OrderRoutingStrategy {

    @Override
    public void route(Order order) {
        if (order.getLimitPrice() == null) {
            throw new IllegalArgumentException(
                "LIMIT order requires a limit price: orderId=" + order.getId());
        }
        order.pendingAck();  // NEW → PENDING_ACK
        // Production: serialise to FIX NewOrderSingle (tag 35=D, tag 40=2, tag 44=limitPrice)
        System.out.printf("[FIX] LIMIT %s %s %s qty=%s price=%s (OrdType=2, TIF=%s)%n",
                order.getSide(), order.getInstrument(), order.getId(),
                order.getRequestedQuantity(), order.getLimitPrice(), order.getTimeInForce());
    }
}
