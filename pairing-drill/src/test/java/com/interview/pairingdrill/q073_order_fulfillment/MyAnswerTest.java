package com.interview.pairingdrill.q073_order_fulfillment;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MyAnswerTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
    private final ShippingNotifier shippingNotifier = Mockito.mock(ShippingNotifier.class);

    private final OrderFulfillmentService service =
            new OrderFulfillmentService(orderRepository, inventoryRepository, shippingNotifier);

    // Write your tests here.
}
