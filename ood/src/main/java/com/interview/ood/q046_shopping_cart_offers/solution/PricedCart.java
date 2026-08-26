package com.interview.ood.q046_shopping_cart_offers.solution;

import java.math.BigDecimal;
import java.util.List;

// Pattern: Decorator — the component interface both the concrete cart and every offer
// wrapper implement, so offers can be stacked in any order the caller chooses without any
// of them knowing whether they're wrapping a plain BasicCart or another decorator.
// Alternative rejected: a single Cart class with a List<Offer> field that it iterates and
// applies internally — rejected because that puts every offer's application order under
// Cart's control (a single loop, a single place), whereas the whole point of this exercise
// is that wrapping order is chosen by the caller and changes the result (BOGOF-then-percent
// differs from percent-then-BOGOF). A field-based list can't express "wrap this decorator
// around that one" as naturally as nested constructor calls do.
// What NOT to do: don't add getOffersApplied() or similar introspection here just because
// it would be convenient for one caller — that couples every implementation to a debugging
// concern instead of keeping the interface to exactly the two things every PricedCart must
// answer: what's in it, and what does it cost.
public interface PricedCart {
    List<CartItem> getItems();
    BigDecimal getTotal();
}
