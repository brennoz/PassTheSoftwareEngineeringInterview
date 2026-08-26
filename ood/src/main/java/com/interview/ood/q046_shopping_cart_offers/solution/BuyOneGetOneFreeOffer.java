package com.interview.ood.q046_shopping_cart_offers.solution;

import java.math.BigDecimal;

// Pattern: Decorator (Concrete Decorator) — finds its target product in the wrapped cart's
// items and subtracts the value of the free units from whatever total the wrapped cart
// already produced. Reads getItems() through the interface, not a concrete BasicCart cast
// — this decorator must work whether it's wrapping BasicCart directly or another decorator,
// since getItems() is guaranteed to pass through unchanged at every layer.
// Alternative rejected: computing "free units" as quantity * 0.5 with a double, then
// rounding — rejected in favour of integer division (quantity / 2) precisely because it
// avoids a rounding decision entirely: 5 items give exactly 2 free units, not a 2.5 that
// then needs a rounding policy no one asked for.
public class BuyOneGetOneFreeOffer extends CartOfferDecorator {

    private final String targetProductName;

    public BuyOneGetOneFreeOffer(PricedCart wrapped, String targetProductName) {
        super(wrapped);
        this.targetProductName = targetProductName;
    }

    // What NOT to do: don't apply this discount if the target product isn't found — silently
    // discounting nothing is correct, but returning wrapped.getTotal().subtract(someDefault)
    // without first confirming the product exists would risk a stale/mismatched discount if
    // a caller ever misspells a product name. Falling through to the unmodified wrapped
    // total makes "no match" and "no discount" the same safe outcome.
    @Override
    public BigDecimal getTotal() {
        BigDecimal total = wrapped.getTotal();
        for (CartItem item : getItems()) {
            if (item.getProductName().equals(targetProductName)) {
                int freeUnits = item.getQuantity() / 2;
                BigDecimal discount = item.getUnitPrice().multiply(BigDecimal.valueOf(freeUnits));
                return total.subtract(discount);
            }
        }
        return total;
    }
}
