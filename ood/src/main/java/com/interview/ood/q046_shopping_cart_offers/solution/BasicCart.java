package com.interview.ood.q046_shopping_cart_offers.solution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Pattern: Decorator (Concrete Component) — the plain, undecorated cart. Its getTotal()
// knows nothing about offers; every discount is layered on top by wrapping this in one or
// more CartOfferDecorator instances. Keeping BasicCart offer-agnostic is what lets new offer
// types be added later without ever touching this class — same Open/Closed reasoning as
// q043's ElevatorState additions not requiring edits to existing states.
public class BasicCart implements PricedCart {

    private final List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item) {
        items.add(item);
    }

    // Why an unmodifiable view, not the live list: decorators call getItems() to inspect
    // quantities (e.g. BuyOneGetOneFreeOffer counting free units) — returning the mutable
    // backing list would let a decorator accidentally (or a future bug) mutate the basket
    // while merely trying to compute a price, corrupting getTotal() for every other
    // decorator in the same stack.
    @Override
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
