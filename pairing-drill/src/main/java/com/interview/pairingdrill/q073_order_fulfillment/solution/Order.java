package com.interview.pairingdrill.q073_order_fulfillment.solution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private Long id;
    private Customer customer;
    private List<OrderItem> items = new ArrayList<>();
    private String status;
    private BigDecimal shippingCost;

    public Order() {
    }

    public Order(Long id, Customer customer, List<OrderItem> items) {
        this.id = id;
        this.customer = customer;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // Why List.copyOf, not returning `items` directly: the seeded version returned the live
    // internal list, so any caller (including a test, harmlessly, or a bug elsewhere in the
    // app, not harmlessly) could mutate an Order's line items without going through the
    // service at all. copyOf is a genuine defensive copy — mutating the returned list throws
    // UnsupportedOperationException instead of silently corrupting order state.
    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    // Why this exists: the seeded service reached order.getCustomer().getAddress()
    // .getCountryCode() directly — three hops into another object's internals (feature envy /
    // Law of Demeter violation), duplicated in two methods. Order legitimately owns its
    // customer/address composition, so it's the right place to answer "is this shipment
    // international," not the service.
    public boolean isInternationalShipment() {
        return customer.getAddress().isInternational();
    }
}
