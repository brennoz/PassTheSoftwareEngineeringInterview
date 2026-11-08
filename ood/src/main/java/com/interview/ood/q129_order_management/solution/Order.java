package com.interview.ood.q129_order_management.solution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// Aggregate root for the order lifecycle.
// The order is the central entity in an OMS — every state transition is triggered by an
// external event (venue ack, execution report, cancel confirm) and must produce an auditable
// record of what changed. All mutation is guarded: no public status setter, no direct list access.
//
// Design decision: status transitions are private (via transitionTo()), not direct field sets.
// Alternative: public setStatus(). Rejected: bypasses canTransitionTo() entirely, allowing
// any caller to skip straight to FILLED — catastrophic for audit trail and event replay.
public class Order {

    private final String id;
    private final OrderSide side;
    private final String instrument;
    private final OrderType orderType;
    private final TimeInForce timeInForce;
    private final BigDecimal requestedQuantity;
    private final Price limitPrice;          // null for MARKET orders

    private OrderStatus status;
    private final List<Execution> executions;
    private final List<OrderEventListener> listeners;

    // Static factory: an order always starts NEW; a public constructor with a status param
    // would allow callers to construct an already-FILLED order, bypassing all lifecycle guards.
    public static Order create(
            OrderSide side,
            String instrument,
            OrderType orderType,
            TimeInForce timeInForce,
            BigDecimal requestedQuantity,
            Price limitPrice) {

        if (requestedQuantity == null || requestedQuantity.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Quantity must be positive");
        if (orderType == OrderType.LIMIT && limitPrice == null)
            throw new IllegalArgumentException("LIMIT order requires a limit price");
        if (orderType == OrderType.MARKET && limitPrice != null)
            throw new IllegalArgumentException("MARKET order must not have a limit price");
        return new Order(side, instrument, orderType, timeInForce, requestedQuantity, limitPrice);
    }

    private Order(OrderSide side, String instrument, OrderType orderType,
                  TimeInForce timeInForce, BigDecimal requestedQuantity, Price limitPrice) {
        this.id                = UUID.randomUUID().toString();
        this.side              = side;
        this.instrument        = instrument;
        this.orderType         = orderType;
        this.timeInForce       = timeInForce;
        this.requestedQuantity = requestedQuantity;
        this.limitPrice        = limitPrice;
        this.status            = OrderStatus.NEW;
        this.executions        = new ArrayList<>();
        this.listeners         = new ArrayList<>();
    }

    // ----- state transition methods (package-private where called only by strategy) --------

    // Called by OrderRoutingStrategy after dispatching to the venue.
    // Package-private: only the strategy layer should trigger this; external callers use submit().
    void pendingAck() {
        transitionTo(OrderStatus.PENDING_ACK);
    }

    public void acknowledge() {
        transitionTo(OrderStatus.ACKNOWLEDGED);
    }

    public void reject(String reason) {
        transitionTo(OrderStatus.REJECTED);
        listeners.forEach(l -> l.onOrderTerminated(this));
    }

    public void expire() {
        transitionTo(OrderStatus.EXPIRED);
        listeners.forEach(l -> l.onOrderTerminated(this));
    }

    public void cancel() {
        transitionTo(OrderStatus.CANCELLED);
        listeners.forEach(l -> l.onOrderTerminated(this));
    }

    // Applies a single fill received from the venue.
    // Auto-promotes to FILLED when remaining quantity reaches zero — the caller must not decide
    // the terminal state based on its own quantity bookkeeping, because that duplicates the
    // arithmetic and can diverge from the order's own execution log.
    public void applyExecution(Execution execution) {
        if (status != OrderStatus.ACKNOWLEDGED && status != OrderStatus.PARTIALLY_FILLED) {
            throw new IllegalStateException(
                "Cannot apply execution to order in status " + status + " (id=" + id + ")");
        }
        executions.add(execution);

        BigDecimal remaining = remainingQuantity();
        int cmp = remaining.compareTo(BigDecimal.ZERO);
        if (cmp < 0) {
            // Overfill — defensive guard; real OMS alerts operations and may reject the fill
            executions.remove(executions.size() - 1); // undo addition
            throw new IllegalStateException(
                "Execution would overfill order " + id + ": wouldRemain=" + remaining);
        } else if (cmp == 0) {
            transitionTo(OrderStatus.FILLED);
            listeners.forEach(l -> l.onExecutionReceived(this, execution));
            listeners.forEach(l -> l.onOrderTerminated(this));
        } else {
            transitionTo(OrderStatus.PARTIALLY_FILLED);
            listeners.forEach(l -> l.onExecutionReceived(this, execution));
        }
    }

    // ----- queries -----------------------------------------------------------

    public BigDecimal remainingQuantity() {
        BigDecimal filled = executions.stream()
                .map(Execution::filledQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return requestedQuantity.subtract(filled);
    }

    public BigDecimal filledQuantity() {
        return executions.stream()
                .map(Execution::filledQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ----- observer registration ---------------------------------------------

    public void addListener(OrderEventListener listener) {
        listeners.add(listener);
    }

    // ----- private helpers ---------------------------------------------------

    private void transitionTo(OrderStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new IllegalStateException(
                "Order " + id + ": illegal transition " + status + " → " + next);
        }
        this.status = next;
    }

    // ----- accessors ---------------------------------------------------------

    public String getId()                      { return id; }
    public OrderSide getSide()                 { return side; }
    public String getInstrument()              { return instrument; }
    public OrderType getOrderType()            { return orderType; }
    public TimeInForce getTimeInForce()        { return timeInForce; }
    public BigDecimal getRequestedQuantity()   { return requestedQuantity; }
    public Price getLimitPrice()               { return limitPrice; }
    public OrderStatus getStatus()             { return status; }
    public List<Execution> getExecutions()     { return Collections.unmodifiableList(executions); }
}
