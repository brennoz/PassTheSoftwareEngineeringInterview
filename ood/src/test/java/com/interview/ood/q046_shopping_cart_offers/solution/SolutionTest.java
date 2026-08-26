package com.interview.ood.q046_shopping_cart_offers.solution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {

    private BasicCart cart;

    @BeforeEach
    void initialise() {
        cart = new BasicCart();
        cart.addItem(new CartItem("Baked Beans", new BigDecimal("1.00"), 5));
        cart.addItem(new CartItem("Bread", new BigDecimal("1.20"), 2));
    }

    @Test
    void basicCartTotalsAllLineItemsWithNoOffers() {
        // Why compareTo, not assertEquals directly on BigDecimal: BigDecimal.equals() is
        // scale-sensitive ("7.40" != "7.4"), a standing rule in this project.
        assertEquals(0, cart.getTotal().compareTo(new BigDecimal("7.40")));
    }

    // What this verifies: an odd quantity (5) yields exactly 2 free units via integer
    // division, not 2.5 rounded up to 3 or down to 2 by some ad-hoc rule. A weaker test
    // using an even quantity would pass under a subtly wrong "quantity * 0.5" rounding
    // implementation too — this test specifically exercises the odd-quantity case that
    // would expose that bug.
    @Test
    void bogofGivesFreeUnitsByIntegerDivisionOnOddQuantity() {
        PricedCart withBogof = new BuyOneGetOneFreeOffer(cart, "Baked Beans");

        // 5 beans -> 2 free units * 1.00 = 2.00 discount; 7.40 - 2.00 = 5.40
        assertEquals(0, withBogof.getTotal().compareTo(new BigDecimal("5.40")));
    }

    @Test
    void bogofOnProductNotInCartLeavesTotalUnchanged() {
        PricedCart withBogof = new BuyOneGetOneFreeOffer(cart, "Butter");

        assertEquals(0, withBogof.getTotal().compareTo(cart.getTotal()));
    }

    // What this verifies: getItems() passes through every decorator layer unchanged,
    // proving decorators recompute price only, never the basket contents. If
    // CartOfferDecorator.getItems() ever stopped delegating (e.g. a decorator started
    // filtering or annotating items), this would be the first test to catch it.
    @Test
    void decoratorsNeverAlterUnderlyingItemList() {
        PricedCart withBogof = new BuyOneGetOneFreeOffer(cart, "Baked Beans");
        PricedCart withBoth = new PercentageOffOffer(withBogof, new BigDecimal("10"));

        assertEquals(cart.getItems().size(), withBoth.getItems().size());
        assertEquals(5, withBoth.getItems().get(0).getQuantity());
    }

    // What this verifies: the actual point of this Decorator exercise — wrapping order
    // changes the result. BOGOF-then-percent discounts the post-BOGOF total; percent-then-
    // BOGOF discounts the pre-BOGOF total instead, then subtracts a flat 2.00 on top. A
    // design that applied offers via a fixed internal loop (rather than caller-chosen
    // wrapping) could not produce two different totals from the same two offers.
    @Test
    void wrappingOrderChangesTheFinalTotal() {
        PricedCart bogofThenPercent = new PercentageOffOffer(
                new BuyOneGetOneFreeOffer(cart, "Baked Beans"), new BigDecimal("10"));
        PricedCart percentThenBogof = new BuyOneGetOneFreeOffer(
                new PercentageOffOffer(cart, new BigDecimal("10")), "Baked Beans");

        // BOGOF first (7.40 - 2.00 = 5.40), then 10% off: 5.40 * 0.90 = 4.86
        assertEquals(0, bogofThenPercent.getTotal().compareTo(new BigDecimal("4.8600")));
        // 10% off first (7.40 * 0.90 = 6.66), then BOGOF's flat 2.00 off: 6.66 - 2.00 = 4.66
        assertEquals(0, percentThenBogof.getTotal().compareTo(new BigDecimal("4.66")));
        assertNotEquals(0, bogofThenPercent.getTotal().compareTo(percentThenBogof.getTotal()));
    }
}
