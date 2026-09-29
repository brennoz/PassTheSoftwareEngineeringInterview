package com.interview.pairingdrill.q073_order_fulfillment;

public class InventoryRecord {

    private String productId;
    private int stockLevel;

    public InventoryRecord() {
    }

    public InventoryRecord(String productId, int stockLevel) {
        this.productId = productId;
        this.stockLevel = stockLevel;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }
}
