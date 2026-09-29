package com.interview.pairingdrill.q073_order_fulfillment.solution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SolutionTest {

    private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
    private final InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
    private final ShippingNotifier shippingNotifier = Mockito.mock(ShippingNotifier.class);

    private final OrderFulfillmentService service =
            new OrderFulfillmentService(orderRepository, inventoryRepository, shippingNotifier);

    private Order domesticOrder() {
        Customer customer = new Customer(1L, new Address("DOMESTIC"));
        List<OrderItem> items = List.of(new OrderItem("sku-1", 2));
        return new Order(100L, customer, items);
    }

    @Test
    void dispatchesDomesticOrderAtBaseCost() throws Exception {
        Order order = domesticOrder();
        Mockito.when(inventoryRepository.findByProductIds(List.of("sku-1")))
               .thenReturn(Map.of("sku-1", new InventoryRecord("sku-1", 10)));

        service.dispatchOrder(order);

        assertEquals("DISPATCHED", order.getStatus());
        assertEquals(0, new BigDecimal("5.00").compareTo(order.getShippingCost()));
        Mockito.verify(inventoryRepository).decrementStock("sku-1", 2);
        Mockito.verify(orderRepository).save(order);
        Mockito.verify(shippingNotifier).sendDispatchNotification(order);
    }

    @Test
    void addsInternationalSurcharge() {
        Customer customer = new Customer(1L, new Address("INTERNATIONAL"));
        Order order = new Order(101L, customer, List.of(new OrderItem("sku-1", 1)));
        Mockito.when(inventoryRepository.findByProductIds(List.of("sku-1")))
               .thenReturn(Map.of("sku-1", new InventoryRecord("sku-1", 10)));

        service.dispatchOrder(order);

        assertEquals(0, new BigDecimal("20.00").compareTo(order.getShippingCost()));
    }

    // This is the regression test for the actual reported ticket: an order must never be
    // marked DISPATCHED (or saved at all) when stock is insufficient — the seeded code did
    // exactly that, because it saved the order before checking inventory.
    @Test
    void doesNotDispatchOrderWhenStockIsInsufficient() {
        Order order = domesticOrder();
        Mockito.when(inventoryRepository.findByProductIds(List.of("sku-1")))
               .thenReturn(Map.of("sku-1", new InventoryRecord("sku-1", 1))); // only 1, order needs 2

        assertThrows(InsufficientStockException.class, () -> service.dispatchOrder(order));

        Mockito.verify(orderRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(inventoryRepository, Mockito.never()).decrementStock(Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    void redispatchSendsRedispatchNotificationOnly() throws Exception {
        Order order = domesticOrder();
        Mockito.when(inventoryRepository.findByProductIds(List.of("sku-1")))
               .thenReturn(Map.of("sku-1", new InventoryRecord("sku-1", 10)));

        service.redispatchOrder(order);

        Mockito.verify(shippingNotifier).sendRedispatchNotification(order);
        Mockito.verify(shippingNotifier, Mockito.never()).sendDispatchNotification(Mockito.any());
    }

    @Test
    void notificationFailureDoesNotUndoAnAlreadyPersistedDispatch() throws Exception {
        Order order = domesticOrder();
        Mockito.when(inventoryRepository.findByProductIds(List.of("sku-1")))
               .thenReturn(Map.of("sku-1", new InventoryRecord("sku-1", 10)));
        Mockito.doThrow(new NotificationException("mail server down"))
               .when(shippingNotifier).sendDispatchNotification(order);

        service.dispatchOrder(order); // must not throw

        assertEquals("DISPATCHED", order.getStatus());
        Mockito.verify(orderRepository).save(order);
        Mockito.verify(inventoryRepository).decrementStock("sku-1", 2);
    }

    @Test
    void getItemsReturnsADefensiveCopy() {
        Order order = domesticOrder();

        assertThrows(UnsupportedOperationException.class, () -> order.getItems().add(new OrderItem("sku-2", 1)));
    }
}
