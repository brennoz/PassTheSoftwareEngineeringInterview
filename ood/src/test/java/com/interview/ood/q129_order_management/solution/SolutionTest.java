package com.interview.ood.q129_order_management.solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Tests verify the order state machine contract and observer callback semantics.
// Each test pins one observable behaviour: status change, guard rejection, or listener invocation.
//
// Why Mockito.verify() for observer tests rather than a hand-written spy:
// verify() asserts both WHICH method was called AND with WHICH arguments — a wrong-event bug
// (firing onOrderTerminated instead of onExecutionReceived for a partial fill) is caught
// immediately. A counter-based spy would only catch whether *any* method was called, not which one.
public class SolutionTest {

    private OrderManager manager;
    private OrderEventListener mockListener;

    @BeforeEach
    void setUp() {
        manager = new OrderManager();
        mockListener = Mockito.mock(OrderEventListener.class);
    }

    @Test
    void newOrder_startsInNewStatus_withFullRemainingQuantity() {
        Order order = limitBuy("100");
        assertEquals(OrderStatus.NEW, order.getStatus());
        // BigDecimal.compareTo: scale-independent equality — "100" == "100.000000"
        assertEquals(0, new BigDecimal("100").compareTo(order.remainingQuantity()));
        assertTrue(order.getExecutions().isEmpty());
    }

    @Test
    void submitLimitOrder_transitionsToPendingAck() {
        Order order = limitBuy("50");
        manager.submit(order);
        assertEquals(OrderStatus.PENDING_ACK, order.getStatus());
    }

    @Test
    void acknowledge_fromPendingAck_transitionsToAcknowledged() {
        Order order = limitBuy("50");
        manager.submit(order);
        order.acknowledge();
        assertEquals(OrderStatus.ACKNOWLEDGED, order.getStatus());
    }

    @Test
    void reject_fromPendingAck_transitionsToRejected_andFiresTerminatedEvent() {
        Order order = limitBuy("50");
        order.addListener(mockListener);
        manager.submit(order);

        order.reject("Credit limit exceeded");

        assertEquals(OrderStatus.REJECTED, order.getStatus());
        verify(mockListener).onOrderTerminated(order);
        // Rejecting must not also fire onExecutionReceived — no fill happened
        verify(mockListener, never()).onExecutionReceived(eq(order), any());
    }

    @Test
    void applyExecution_partialFill_transitionsToPartiallyFilled_withCorrectRemaining() {
        Order order = limitBuy("100");
        manager.submit(order);
        order.acknowledge();

        order.applyExecution(exec("EX-001", "185.50", "60")); // fill 60 of 100

        assertEquals(OrderStatus.PARTIALLY_FILLED, order.getStatus());
        // Remaining = 100 - 60 = 40
        assertEquals(0, new BigDecimal("40").compareTo(order.remainingQuantity()));
        assertEquals(1, order.getExecutions().size());
    }

    @Test
    void applyExecution_fullFill_transitionsToFilled_andFiresBothCallbacks() {
        Order order = limitBuy("100");
        order.addListener(mockListener);
        manager.submit(order);
        order.acknowledge();

        Execution fill = exec("EX-001", "185.50", "100");
        order.applyExecution(fill);

        assertEquals(OrderStatus.FILLED, order.getStatus());
        assertEquals(0, BigDecimal.ZERO.compareTo(order.remainingQuantity()));
        // Full fill must fire BOTH onExecutionReceived (the fill event) AND onOrderTerminated (terminal state)
        // A common bug: only firing onOrderTerminated, losing the execution details for the audit trail.
        verify(mockListener).onExecutionReceived(order, fill);
        verify(mockListener).onOrderTerminated(order);
    }

    @Test
    void cancel_fromAcknowledged_transitionsToCancelled() {
        Order order = limitBuy("50");
        manager.submit(order);
        order.acknowledge();
        order.cancel();
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancel_fromRejected_throwsIllegalStateException_statusUnchanged() {
        Order order = limitBuy("50");
        manager.submit(order);
        order.reject("Risk limit");

        // Verify the guard fires and the status is NOT silently changed
        IllegalStateException ex = assertThrows(IllegalStateException.class, order::cancel);
        assertTrue(ex.getMessage().contains("REJECTED"),
                "Exception message should name the invalid source status");
        assertEquals(OrderStatus.REJECTED, order.getStatus(),
                "Status must not change after a blocked transition");
    }

    @Test
    void applyExecution_overfill_throwsAndRollsBackExecution() {
        Order order = limitBuy("10");
        manager.submit(order);
        order.acknowledge();

        assertThrows(IllegalStateException.class,
                () -> order.applyExecution(exec("EX-OVR", "185.50", "15"))); // 15 > 10
        // The failed execution must not be recorded — the order's internal list is unchanged
        assertTrue(order.getExecutions().isEmpty(),
                "Overfill must not be added to the execution list");
        assertEquals(OrderStatus.ACKNOWLEDGED, order.getStatus());
    }

    // ---------- helpers ----------

    private static Order limitBuy(String qty) {
        return Order.create(OrderSide.BUY, "AAPL.US",
                OrderType.LIMIT, TimeInForce.DAY,
                new BigDecimal(qty), Price.of("USD", "185.50"));
    }

    private static Execution exec(String id, String price, String qty) {
        return new Execution(id, Price.of("USD", price), new BigDecimal(qty), Instant.now());
    }
}
