package com.interview.pairingdrill.q073_order_fulfillment.solution;

// Why unchecked, unlike NotificationException: this represents a genuine business-rule
// violation the caller should not be forced to handle at every call site — it should propagate
// and abort the dispatch, not be routinely caught inline.
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productId) {
        super("Insufficient stock for product " + productId);
    }
}
