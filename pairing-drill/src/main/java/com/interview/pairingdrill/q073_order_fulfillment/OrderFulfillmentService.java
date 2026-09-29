package com.interview.pairingdrill.q073_order_fulfillment;

import java.math.BigDecimal;

public class OrderFulfillmentService {

    private static final BigDecimal DOMESTIC_BASE_COST = new BigDecimal("5.00");

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
        BigDecimal cost = DOMESTIC_BASE_COST;
        if (order.getCustomer().getAddress().getCountryCode().equals("INTERNATIONAL")) {
            cost = cost.add(new BigDecimal("15.00"));
        }
        order.setShippingCost(cost);
        order.setStatus("DISPATCHED");
        orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            InventoryRecord record = inventoryRepository.findByProductId(item.getProductId());
            inventoryRepository.decrementStock(item.getProductId(), item.getQuantity());
        }

        try {
            shippingNotifier.sendDispatchNotification(order);
        } catch (Exception e) {
        }
    }

    public void redispatchOrder(Order order) {
        BigDecimal cost = DOMESTIC_BASE_COST;
        if (order.getCustomer().getAddress().getCountryCode().equals("INTERNATIONAL")) {
            cost = cost.add(new BigDecimal("15.00"));
        }
        order.setShippingCost(cost);
        order.setStatus("DISPATCHED");
        orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            InventoryRecord record = inventoryRepository.findByProductId(item.getProductId());
            inventoryRepository.decrementStock(item.getProductId(), item.getQuantity());
        }

        try {
            shippingNotifier.sendRedispatchNotification(order);
        } catch (Exception e) {
        }
    }
}
