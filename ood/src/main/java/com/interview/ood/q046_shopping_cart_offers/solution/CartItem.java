package com.interview.ood.q046_shopping_cart_offers.solution;

import java.math.BigDecimal;

// Why a plain value object, not a richer entity with its own discount logic: an item is a
// fact about what's in the basket (what, how much each, how many). Discount behaviour
// belongs in the decorators that wrap the cart, not scattered across every line item —
// otherwise a "10% off everything" offer would need to reach into every CartItem and mutate
// its effective price, which breaks the moment two offers need to stack in a specific order.
public class CartItem {

    private final String productName;
    private final BigDecimal unitPrice;
    private final int quantity;

    public CartItem(String productName, BigDecimal unitPrice, int quantity) {
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
