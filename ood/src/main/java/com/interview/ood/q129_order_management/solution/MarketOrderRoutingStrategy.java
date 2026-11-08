package com.interview.ood.q129_order_management.solution;

// Market order: execute immediately at the best available price — no limitPrice needed.
// What NOT to do: route a MARKET order that carries a limitPrice. Some venues reject it,
// some silently ignore it, some treat it as a LIMIT order — all three outcomes are wrong
// and could produce unexpected fills or outright rejection at a live venue.
public class MarketOrderRoutingStrategy implements OrderRoutingStrategy {

    @Override
    public void route(Order order) {
        if (order.getLimitPrice() != null) {
            throw new IllegalArgumentException(
                "MARKET order must not carry a limit price: orderId=" + order.getId());
        }
        order.pendingAck();  // NEW → PENDING_ACK: venue has received the order
        // Production: serialise to FIX NewOrderSingle (tag 35=D, tag 40=1) and publish to FIX session
        System.out.printf("[FIX] MARKET %s %s %s qty=%s (OrdType=1, TIF=%s)%n",
                order.getSide(), order.getInstrument(), order.getId(),
                order.getRequestedQuantity(), order.getTimeInForce());
    }
}
