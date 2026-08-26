package com.interview.ood.q046_shopping_cart_offers.solution;

import java.util.List;

// Pattern: Decorator (base Decorator) — holds the wrapped PricedCart and forwards
// getItems() unchanged. Every concrete offer extends this instead of implementing
// PricedCart directly, so "pass items through untouched" is written exactly once instead of
// copy-pasted into every offer subclass — a repeat of the mistake this project's OOD
// convention already flags: duplicating a fixed rule (here, "decorators never alter the
// item list") into N places instead of one.
// What NOT to do: don't override getItems() in a concrete offer just to "helpfully" filter
// or annotate it (e.g. marking which items are discounted) — that's a reporting concern,
// not this class's job, and it would make getItems() return a different view depending on
// which offers happen to be wrapped around a cart, which is exactly the kind of surprising
// stateful side effect Decorator is supposed to avoid.
public abstract class CartOfferDecorator implements PricedCart {

    protected final PricedCart wrapped;

    protected CartOfferDecorator(PricedCart wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public List<CartItem> getItems() {
        return wrapped.getItems();
    }
}
