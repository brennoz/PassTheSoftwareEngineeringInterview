package com.interview.pairingdrill.q073_order_fulfillment;

public interface InventoryRepository {

    InventoryRecord findByProductId(String productId);

    void decrementStock(String productId, int quantity);
}
