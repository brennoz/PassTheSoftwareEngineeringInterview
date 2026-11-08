package com.interview.ood.q129_order_management.solution;

// Observer interface for order lifecycle events.
// Alternative considered: single onEvent(OrderEvent) with a sealed hierarchy of event types.
// Rejected for this scope: distinct methods make Mockito.verify() assertions clearer and
// avoid casting in implementations — better signal in an OOD assessment where the reviewer
// is evaluating interface design, not event-type dispatch sophistication.
public interface OrderEventListener {

    void onOrderSubmitted(Order order);

    void onExecutionReceived(Order order, Execution execution);

    // Called when the order reaches any terminal status: FILLED, CANCELLED, REJECTED, or EXPIRED.
    void onOrderTerminated(Order order);
}
