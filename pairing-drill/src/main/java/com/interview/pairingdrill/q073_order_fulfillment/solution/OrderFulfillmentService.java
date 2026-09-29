package com.interview.pairingdrill.q073_order_fulfillment.solution;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OrderFulfillmentService {

    private static final BigDecimal DOMESTIC_BASE_COST = new BigDecimal("5.00");
    private static final BigDecimal INTERNATIONAL_SURCHARGE = new BigDecimal("15.00");

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final ShippingNotifier shippingNotifier;

    public OrderFulfillmentService(OrderRepository orderRepository,
                                    InventoryRepository inventoryRepository,
                                    ShippingNotifier shippingNotifier) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.shippingNotifier = shippingNotifier;
    }

    public void dispatchOrder(Order order) {
        fulfill(order, DispatchType.INITIAL);
    }

    public void redispatchOrder(Order order) {
        fulfill(order, DispatchType.REDISPATCH);
    }

    // Why extracted: dispatchOrder/redispatchOrder were byte-for-byte identical except which
    // notification fires. A DispatchType parameter collapses the duplication to one method
    // instead of two that have to be kept in sync by hand.
    private void fulfill(Order order, DispatchType dispatchType) {
        // Why inventory is reserved BEFORE the order is marked DISPATCHED and saved — this is
        // the actual fix for the reported ticket. The seeded code saved the order as
        // dispatched first, then decremented stock after — if anything went wrong in between
        // (or stock was simply insufficient), the order was already recorded as dispatched
        // with nothing actually reserved. Reordering so inventory succeeds first means a
        // failure here never produces an order that claims to be dispatched.
        reserveInventory(order);

        order.setShippingCost(calculateShippingCost(order));
        order.setStatus("DISPATCHED");
        orderRepository.save(order);

        notify(order, dispatchType);
    }

    private void reserveInventory(Order order) {
        List<String> productIds = order.getItems().stream().map(OrderItem::getProductId).toList();
        // Why batched: the seeded code called findByProductId once per item inside the loop
        // below (N+1). One call for every product ID on the order turns N round trips into 1.
        Map<String, InventoryRecord> records = inventoryRepository.findByProductIds(productIds);

        // Two passes, deliberately: validate every item's stock is sufficient first, THEN
        // decrement. The seeded code fetched a record and never checked it — validate-then-act
        // in one combined pass would decrement some items and only then discover a later item
        // is short, leaving inventory partially reserved for an order that's about to be
        // rejected anyway.
        for (OrderItem item : order.getItems()) {
            InventoryRecord record = records.get(item.getProductId());
            if (record == null || record.getStockLevel() < item.getQuantity()) {
                throw new InsufficientStockException(item.getProductId());
            }
        }
        for (OrderItem item : order.getItems()) {
            inventoryRepository.decrementStock(item.getProductId(), item.getQuantity());
        }
    }

    private BigDecimal calculateShippingCost(Order order) {
        BigDecimal cost = DOMESTIC_BASE_COST;
        if (order.isInternationalShipment()) {
            cost = cost.add(INTERNATIONAL_SURCHARGE);
        }
        return cost;
    }

    private void notify(Order order, DispatchType dispatchType) {
        try {
            if (dispatchType == DispatchType.INITIAL) {
                shippingNotifier.sendDispatchNotification(order);
            } else {
                shippingNotifier.sendRedispatchNotification(order);
            }
        } catch (NotificationException e) {
            // Deliberately still not rethrown — a failed customer notification shouldn't undo
            // an already-persisted, already-reserved dispatch. But silently discarding it (the
            // seeded bug: bare `catch (Exception e) {}`) hides a real delivery problem from
            // anyone who could act on it. At minimum it must be observable; a real system would
            // route this to structured logging or a dead-letter queue for retry, not stdout.
            System.err.println("Notification failed for order " + order.getId() + ": " + e.getMessage());
        }
    }

    private enum DispatchType { INITIAL, REDISPATCH }
}
