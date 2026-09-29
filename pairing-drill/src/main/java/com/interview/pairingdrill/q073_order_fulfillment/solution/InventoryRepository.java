package com.interview.pairingdrill.q073_order_fulfillment.solution;

import java.util.List;
import java.util.Map;

public interface InventoryRepository {

    // Why added: the seeded code called findByProductId once per item inside a for loop — a
    // classic N+1 query pattern. Batching into one call taking every product ID for the order
    // turns N round trips into 1, and it's needed anyway now that stock is actually checked
    // before decrementing.
    Map<String, InventoryRecord> findByProductIds(List<String> productIds);

    void decrementStock(String productId, int quantity);
}
