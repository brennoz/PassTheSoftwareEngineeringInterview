package com.interview.ood.q129_order_management.solution;

import java.math.BigDecimal;
import java.time.Instant;

// Runnable demonstration of the OMS order lifecycle end-to-end.
// Scenarios: full fill in two partial executions, cancel after ack, rejection, invalid guard.
public class OrderMain {

    public static void main(String[] args) {
        OrderManager manager = new OrderManager();
        manager.registerGlobalListener(new LoggingListener());

        // Scenario 1: LIMIT BUY — two partial fills → FILLED
        System.out.println("=== Scenario 1: LIMIT BUY — two partial fills → FILLED ===");
        Order limitBuy = Order.create(
                OrderSide.BUY, "AAPL.US", OrderType.LIMIT, TimeInForce.DAY,
                new BigDecimal("100"), Price.of("USD", "185.50"));
        manager.submit(limitBuy);                                       // NEW → PENDING_ACK
        limitBuy.acknowledge();                                         // PENDING_ACK → ACKNOWLEDGED
        limitBuy.applyExecution(exec("EX-001", "185.48", "60"));       // → PARTIALLY_FILLED
        limitBuy.applyExecution(exec("EX-002", "185.50", "40"));       // → FILLED
        System.out.printf("  remaining=%s filled=%s status=%s%n%n",
                limitBuy.remainingQuantity(), limitBuy.filledQuantity(), limitBuy.getStatus());

        // Scenario 2: MARKET SELL — cancel after acknowledgement
        System.out.println("=== Scenario 2: MARKET SELL IOC — cancel after ack ===");
        Order marketSell = Order.create(
                OrderSide.SELL, "EURUSD", OrderType.MARKET, TimeInForce.IOC,
                new BigDecimal("10000"), null);
        manager.submit(marketSell);
        marketSell.acknowledge();
        marketSell.cancel();
        System.out.printf("  status=%s%n%n", marketSell.getStatus());

        // Scenario 3: LIMIT BUY — rejected by venue (risk limit)
        System.out.println("=== Scenario 3: LIMIT BUY — rejected by venue ===");
        Order rejected = Order.create(
                OrderSide.BUY, "TSLA.US", OrderType.LIMIT, TimeInForce.GTC,
                new BigDecimal("500"), Price.of("USD", "200.00"));
        manager.submit(rejected);
        rejected.reject("Exceeds single-order risk limit");
        System.out.printf("  status=%s%n%n", rejected.getStatus());

        // Scenario 4: invalid transition guard — cannot cancel a FILLED order
        System.out.println("=== Scenario 4: invalid transition guard ===");
        try {
            limitBuy.cancel();
            System.out.println("  ERROR: expected IllegalStateException");
        } catch (IllegalStateException e) {
            System.out.println("  Correctly blocked: " + e.getMessage());
        }

        // Scenario 5: overfill guard
        System.out.println("\n=== Scenario 5: overfill guard ===");
        Order small = Order.create(
                OrderSide.BUY, "MSFT.US", OrderType.LIMIT, TimeInForce.DAY,
                new BigDecimal("10"), Price.of("USD", "300.00"));
        manager.submit(small);
        small.acknowledge();
        try {
            small.applyExecution(exec("EX-OVR", "300.00", "15")); // 15 > 10 — overfill
            System.out.println("  ERROR: expected overfill exception");
        } catch (IllegalStateException e) {
            System.out.println("  Correctly blocked: " + e.getMessage());
        }
        System.out.printf("  executions after blocked overfill: %d%n", small.getExecutions().size());
    }

    private static Execution exec(String id, String price, String qty) {
        return new Execution(id, Price.of("USD", price), new BigDecimal(qty), Instant.now());
    }

    private static class LoggingListener implements OrderEventListener {
        @Override public void onOrderSubmitted(Order o) {
            System.out.printf("  [EVT] submitted %s  type=%s  tif=%s  qty=%s%n",
                    o.getId().substring(0, 8), o.getOrderType(), o.getTimeInForce(), o.getRequestedQuantity());
        }
        @Override public void onExecutionReceived(Order o, Execution ex) {
            System.out.printf("  [EVT] execution %s: filled=%s @ %s  remaining=%s%n",
                    ex.executionId(), ex.filledQuantity(), ex.executionPrice(), o.remainingQuantity());
        }
        @Override public void onOrderTerminated(Order o) {
            System.out.printf("  [EVT] terminal → %s  id=%s%n", o.getStatus(), o.getId().substring(0, 8));
        }
    }
}
